#!/usr/bin/env bash
set -euo pipefail
project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$project_root"
version="$(mvn -q -Dstyle.color=never help:evaluate -Dexpression=project.version -DforceStdout)"
artifact="$project_root/target/ElytraFly-$version.jar"
if [[ ! -f "$artifact" || ! -f target/test-classes/cn/ericcraft/elytraFly/ApiSmoke.class ]]; then
  echo 'Run mvn clean verify first (the production JAR and harness must already exist).' >&2
  exit 1
fi
if (( $# == 0 )); then
  set -- 1.9 1.9.4 1.10.2 1.11.2 1.12.2 1.13.2 1.14.4 1.15.2 1.16.5 1.17.1 1.18.2 1.19.4 1.20.3 1.20.6 1.21 1.21.11 26.1.2 26.2 26.3
fi
mkdir -p target/compatibility
before="$(shasum -a 256 "$artifact")"
for api in "$@"; do
  [[ "$api" =~ ^[0-9]+\.[0-9]+(\.[0-9]+)?$ ]] || { echo "Invalid API version: $api" >&2; exit 1; }
  classpath_file="$project_root/target/compatibility/$api.classpath"
  mvn -q --no-transfer-progress -f scripts/compatibility/pom.xml \
    "-Dspigot.version=$api-R0.1-SNAPSHOT" \
    org.apache.maven.plugins:maven-dependency-plugin:3.7.0:build-classpath \
    "-Dmdep.outputFile=$classpath_file"
  echo "Checking API $api with unchanged artifact"
  java -cp "$artifact:$project_root/target/test-classes:$(cat "$classpath_file")" \
    cn.ericcraft.elytraFly.ApiSmoke "$artifact" 2>&1 | tee "target/compatibility/$api.log"
done
[[ "$before" == "$(shasum -a 256 "$artifact")" ]] || { echo 'Artifact changed during verification' >&2; exit 1; }
echo "$before" > target/compatibility/artifact.sha256
