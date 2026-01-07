#!/usr/bin/env python3
import json
import requests
import sys
import os
from datetime import datetime
from dotenv import load_dotenv

load_dotenv()

GITHUB_TOKEN = os.getenv("GITHUB_TOKEN", "")
REPO_OWNER = os.getenv("REPO_OWNER", "MihailRis")
REPO_NAME = os.getenv("REPO_NAME", "voxelcore")
OUTPUT_FILE = os.getenv("OUTPUT_FILE", "releases.json")

if not GITHUB_TOKEN:
    print("[-] GITHUB_TOKEN не установлен в .env файле")
    sys.exit(1)

PLATFORMS = {
    "windows": {"pattern": "win64", "ext": ".zip"},
    "macos": {"pattern": "macos", "ext": ".dmg"},
    "linux": {"pattern": "AppImage", "ext": ".AppImage"}
}

def fetch_releases():
    url = f"https://api.github.com/repos/{REPO_OWNER}/{REPO_NAME}/releases"
    headers = {
        "Accept": "application/vnd.github.v3+json",
        "User-Agent": "ReleaseParser"
    }

    if GITHUB_TOKEN and GITHUB_TOKEN != "your_github_token_here":
        headers["Authorization"] = f"Bearer {GITHUB_TOKEN}"

    print(f"[*] Запрос к {url}")

    try:
        response = requests.get(url, headers=headers, timeout=10)
        response.raise_for_status()
        print(f"[+] Получено {response.status_code}")
        return response.json()
    except requests.exceptions.HTTPError as e:
        print(f"[-] Ошибка HTTP: {e}")
        if response.status_code == 403:
            print("[-] Rate limit исчерпан. Используй GitHub токен!")
        sys.exit(1)
    except Exception as e:
        print(f"[-] Ошибка: {e}")
        sys.exit(1)

def parse_releases(releases):
    result = []

    for release in releases:
        if release.get("draft"):
            print(f"[*] Пропускаем draft: {release['tag_name']}")
            continue

        tag_name = release["tag_name"]
        assets = release.get("assets", [])

        if not assets:
            print(f"[*] Нет assets для {tag_name}")
            continue

        filtered_assets = []

        for asset in assets:
            name = asset["name"]
            url = asset["browser_download_url"]
            size = asset["size"]

            for platform, config in PLATFORMS.items():
                if config["pattern"] in name and name.endswith(config["ext"]):
                    filtered_assets.append({
                        "name": name,
                        "browser_download_url": url,
                        "size": size
                    })
                    print(f"[+] {tag_name}: {name} ({size} bytes)")
                    break

        if filtered_assets:
            result.append({
                "tag_name": tag_name,
                "draft": False,
                "assets": filtered_assets
            })

    return result

def save_releases(releases):
    try:
        with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
            json.dump(releases, f, indent=2, ensure_ascii=False)
        print(f"\n[+] Сохранено в {OUTPUT_FILE}")
        print(f"[+] Релизов: {len(releases)}")

        total_assets = sum(len(r["assets"]) for r in releases)
        print(f"[+] Всего assets: {total_assets}")

    except Exception as e:
        print(f"[-] Ошибка при сохранении: {e}")
        sys.exit(1)

def main():
    print(f"[*] GitHub Releases Parser")
    print(f"[*] Репозиторий: {REPO_OWNER}/{REPO_NAME}")
    print(f"[*] Вывод: {OUTPUT_FILE}")
    print(f"[*] Время: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print()

    releases = fetch_releases()
    print(f"[+] Получено релизов: {len(releases)}\n")

    print("[*] Парсим релизы...")
    parsed = parse_releases(releases)

    print()
    save_releases(parsed)

if __name__ == "__main__":
    main()