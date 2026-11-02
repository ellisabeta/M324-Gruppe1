#!/usr/bin/env python3
"""Z1: Sammelt Kennzahlen aus Git, GitHub Actions, Pull Requests und dem Quellcode.

Aufruf (aus dem Repo-Root):
    python3 Code/P2/metrics/collect_metrics.py

Optional: GITHUB_TOKEN setzen, falls das API-Limit (60 Anfragen/Stunde) erreicht wird.
Ausgabe: Dokumentation/Z1-Kennzahlen-Daten.md und Code/P2/metrics/metrics.json
"""
import json
import os
import re
import statistics
import subprocess
import sys
import urllib.request
from collections import Counter
from datetime import datetime, timezone
from pathlib import Path

REPO = "ellisabeta/M324-Gruppe1"
ROOT = Path(__file__).resolve().parents[3]
SERVICES = ["flughafen-service", "flug-service"]
DEPLOY_JOB = "Docker Deployment"
MAIN_WORKFLOW = ".github/workflows/ci.yml"


def api(path):
    req = urllib.request.Request(f"https://api.github.com/repos/{REPO}/{path}")
    req.add_header("Accept", "application/vnd.github+json")
    if os.environ.get("GITHUB_TOKEN"):
        req.add_header("Authorization", f"Bearer {os.environ['GITHUB_TOKEN']}")
    try:
        with urllib.request.urlopen(req) as res:
            return json.load(res)
    except urllib.error.HTTPError as err:
        if err.code == 403 and err.headers.get("X-RateLimit-Remaining") == "0":
            reset = datetime.fromtimestamp(int(err.headers["X-RateLimit-Reset"])).strftime("%H:%M")
            sys.exit(f"GitHub-API-Limit erreicht (60 Anfragen/Stunde ohne Token). "
                     f"Wieder möglich ab {reset} Uhr oder GITHUB_TOKEN setzen.")
        raise


def ts(value):
    return datetime.fromisoformat(value.replace("Z", "+00:00"))


def hours(delta):
    return round(delta.total_seconds() / 3600, 1)


def git(*args):
    return subprocess.run(["git", *args], cwd=ROOT, capture_output=True, text=True, check=True).stdout


def ci_metrics():
    runs = api("actions/runs?per_page=100")["workflow_runs"]
    # "dynamic" = von GitHub selbst gestartete Runs (z.B. Copilot Review), keine Pipeline von uns
    done = [r for r in runs if r["conclusion"] in ("success", "failure") and r["event"] != "dynamic"]
    durations = [(ts(r["updated_at"]) - ts(r["created_at"])).total_seconds() / 60 for r in done]

    # MTTR: fehlgeschlagener Run -> naechster erfolgreicher Run auf gleichem Workflow + Branch
    restore_times = []
    for r in done:
        if r["conclusion"] != "failure":
            continue
        fixes = [f for f in done
                 if f["conclusion"] == "success" and f["path"] == r["path"]
                 and f["head_branch"] == r["head_branch"] and ts(f["created_at"]) > ts(r["created_at"])]
        if fixes:
            fix = min(fixes, key=lambda f: ts(f["created_at"]))
            restore_times.append(ts(fix["created_at"]) - ts(r["created_at"]))

    # Deployments: Runs von main/Tags, in denen der Job "Docker Deployment" gelaufen ist
    deployments = []
    for r in done:
        if r["path"] != MAIN_WORKFLOW or r["event"] != "push" or not (r["head_branch"] == "main" or r["head_branch"].startswith("v")):
            continue
        for job in api(f"actions/runs/{r['id']}/jobs")["jobs"]:
            if job["name"] == DEPLOY_JOB and job["conclusion"] in ("success", "failure"):
                deployments.append({"date": job["started_at"][:10], "ref": r["head_branch"],
                                    "conclusion": job["conclusion"]})

    main_runs = [r for r in done if r["head_branch"] == "main"]
    main_ci = [r for r in main_runs if r["path"] == MAIN_WORKFLOW]
    failures = Counter(r["path"].split("/")[-1] for r in done if r["conclusion"] == "failure")
    return {
        "runs_total": len(done),
        "runs_success": sum(r["conclusion"] == "success" for r in done),
        "success_rate": round(100 * sum(r["conclusion"] == "success" for r in done) / len(done), 1),
        "main_failure_rate": round(100 * sum(r["conclusion"] == "failure" for r in main_runs) / len(main_runs), 1),
        "main_ci_failure_rate": round(100 * sum(r["conclusion"] == "failure" for r in main_ci) / len(main_ci), 1),
        "main_ci_runs": len(main_ci),
        "failures_by_workflow": dict(failures.most_common()),
        "duration_median_min": round(statistics.median(durations), 1),
        "duration_max_min": round(max(durations), 1),
        "mttr_median_h": hours(statistics.median(restore_times)) if restore_times else None,
        "failures_restored": len(restore_times),
        "deployments": deployments,
        "first_run": min(r["created_at"] for r in done)[:10],
        "last_run": max(r["created_at"] for r in done)[:10],
    }


def pr_metrics():
    prs = api("pulls?state=closed&per_page=100")
    merged = [p for p in prs if p["merged_at"]]
    lead = [ts(p["merged_at"]) - ts(p["created_at"]) for p in merged]
    return {
        "closed": len(prs),
        "merged": len(merged),
        "lead_time_median_h": hours(statistics.median(lead)) if lead else None,
        "lead_time_max_h": hours(max(lead)) if lead else None,
    }


def git_metrics():
    log = git("log", "--no-merges", "--format=%an|%aI|%s").strip().splitlines()
    commits = [line.split("|", 2) for line in log]
    weeks = Counter(datetime.fromisoformat(d).strftime("%G-W%V") for _, d, _ in commits)
    fixes = sum(bool(re.match(r"(?i)(fix|hotfix|bugfix)\b", s)) for _, _, s in commits)
    return {
        "commits": len(commits),
        "merges": len(git("log", "--merges", "--format=%H").split()),
        "per_author": dict(Counter(a for a, _, _ in commits).most_common()),
        "per_week": dict(sorted(weeks.items())),
        "fix_commits": fixes,
        "fix_share": round(100 * fixes / len(commits), 1),
    }


def code_metrics():
    result = {}
    for service in SERVICES:
        base = ROOT / "Code/P2" / service / "src"
        loc = lambda d: sum(len(f.read_text().splitlines()) for f in (base / d).rglob("*.java"))
        tests = sum(f.read_text().count("@Test") for f in (base / "test").rglob("*.java"))
        main_loc, test_loc = loc("main"), loc("test")
        result[service] = {"main_loc": main_loc, "test_loc": test_loc, "tests": tests,
                           "test_ratio": round(test_loc / main_loc, 2)}
    return result


def report(m):
    ci, pr, g, code = m["ci"], m["pr"], m["git"], m["code"]
    ok_deploys = [d for d in ci["deployments"] if d["conclusion"] == "success"]
    lines = [
        "# Z1 – Gemessene Kennzahlen (automatisch erzeugt)",
        "",
        f"Erzeugt am {m['generated']} mit `Code/P2/metrics/collect_metrics.py`. Nicht von Hand bearbeiten.",
        f"Zeitraum CI-Daten: {ci['first_run']} bis {ci['last_run']}.",
        "",
        "## Delivery (DORA)",
        "| Kennzahl | Wert |",
        "|---|---|",
        f"| Deployments (Job \"{DEPLOY_JOB}\", erfolgreich / total) | {len(ok_deploys)} / {len(ci['deployments'])} |",
        f"| Lead Time PR erstellt → gemerged (Median) | {pr['lead_time_median_h']} h |",
        f"| Lead Time PR (Maximum) | {pr['lead_time_max_h']} h |",
        f"| Change Failure Rate: Fehlerrate aller Workflows auf `main` | {ci['main_failure_rate']} % |",
        f"| Change Failure Rate: Fehlerrate `ci.yml` auf `main` | {ci['main_ci_failure_rate']} % ({ci['main_ci_runs']} Runs) |",
        f"| Time to Restore (Median, fehlgeschlagen → wieder grün) | "
        + (f"{ci['mttr_median_h']} h ({ci['failures_restored']} Fälle)" if ci["failures_restored"] else "keine messbaren Fälle") + " |",
        "",
        "## Pipeline",
        "| Kennzahl | Wert |",
        "|---|---|",
        f"| Abgeschlossene Workflow-Runs | {ci['runs_total']} |",
        f"| Erfolgsquote | {ci['success_rate']} % ({ci['runs_success']}/{ci['runs_total']}) |",
        f"| Dauer pro Run (Median / Maximum) | {ci['duration_median_min']} / {ci['duration_max_min']} min |",
        "",
        "Fehlgeschlagene Runs pro Workflow: " + ", ".join(f"{w}: {n}" for w, n in ci["failures_by_workflow"].items()),
        "",
        "## Qualität",
        "| Service | Produktiv-LOC | Test-LOC | Test/Code-Verhältnis | Anzahl Tests |",
        "|---|---|---|---|---|",
        *[f"| {s} | {c['main_loc']} | {c['test_loc']} | {c['test_ratio']} | {c['tests']} |" for s, c in code.items()],
        "",
        "## Team & Prozess",
        "| Kennzahl | Wert |",
        "|---|---|",
        f"| Commits (ohne Merges) / Merge-Commits | {g['commits']} / {g['merges']} |",
        f"| Pull Requests gemerged / geschlossen | {pr['merged']} / {pr['closed']} |",
        f"| Anteil Fix-Commits | {g['fix_share']} % ({g['fix_commits']}) |",
        "",
        "Commits pro Person: " + ", ".join(f"{a}: {n}" for a, n in g["per_author"].items()),
        "",
        "Commits pro Kalenderwoche: " + ", ".join(f"{w}: {n}" for w, n in g["per_week"].items()),
        "",
    ]
    return "\n".join(lines)


if __name__ == "__main__":
    metrics = {
        "generated": datetime.now(timezone.utc).strftime("%Y-%m-%d %H:%M UTC"),
        "ci": ci_metrics(),
        "pr": pr_metrics(),
        "git": git_metrics(),
        "code": code_metrics(),
    }
    (ROOT / "Code/P2/metrics/metrics.json").write_text(json.dumps(metrics, indent=2, ensure_ascii=False))
    (ROOT / "Dokumentation/Z1-Kennzahlen-Daten.md").write_text(report(metrics))
    print(report(metrics))
