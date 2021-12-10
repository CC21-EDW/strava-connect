# repository-template-java
A template to use when starting a new open source project.

## perform a repository wide search and replace for "repository-template-java" and the "target-repo-name"

e.g. by using

```
cp -R repository-template-java/ new-name && cd new-name && git config --local --unset remote.origin.url && git config --local --add remote.origin.url git@github.com:baloise/new-name.git && git reset --hard $(git commit-tree FETCH_HEAD^{tree} -m "Initial contribution") &&  git grep -l 'repository-template-java' | xargs sed -i '' -e 's/repository-template-java/new-name/g' && mvn clean verify && git add -A && git commit -m "Rename from template to new-name" && cd ..
```
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/6888b72aaf354f4496af9b068d424eb0)](https://www.codacy.com/gh/CC21-EDW/strava-connect/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=CC21-EDW/strava-connect&amp;utm_campaign=Badge_Grade)
[![DepShield Badge](https://depshield.sonatype.org/badges/baloise/repository-template-java/depshield.svg)](https://depshield.github.io)
![Build Status](https://github.com/baloise/repository-template-java/workflows/CI/badge.svg)

## the [docs](docs/index.md)

## releasing

Run e.g. on main: `mvn -B release:prepare` e.g. via [![Gitpod ready-to-code](https://img.shields.io/badge/Gitpod-ready--to--code-blue?logo=gitpod)](https://gitpod.io#https://github.com/baloise/repository-template-java)

Subsequently the GitHub action worksflow "create release" will pick up the published tag and release and deploy the artifacts in the Github package registry.
