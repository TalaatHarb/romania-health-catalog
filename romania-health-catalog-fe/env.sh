#!/bin/sh
# Generates env-config.js (window._env_) from the keys in the .env file.
# A key's value comes from the environment variable of the same name when it's set and not empty,
# otherwise from the .env file.
#
#   ENV_FILE     .env file listing the keys and their defaults (default ./.env)
#   OUTPUT_FILE  generated script (default ./env-config.js)
#
# The FE image runs it from /docker-entrypoint.d before nginx starts: docker run -e API_URL=https://api...
set -eu

ENV_FILE="${ENV_FILE:-./.env}"
OUTPUT_FILE="${OUTPUT_FILE:-./env-config.js}"

if [ ! -r "$ENV_FILE" ]; then
  echo "env.sh: can't read $ENV_FILE" >&2
  exit 1
fi

# escapes a value for a double quoted JS string
js_escape() {
  printf '%s' "$1" | sed -e 's/\\/\\\\/g' -e 's/"/\\"/g'
}

tmp_file="$OUTPUT_FILE.tmp"
{
  echo "window._env_ = {"
  # '|| [ -n "$line" ]' keeps a last line without a trailing newline
  while IFS= read -r line || [ -n "$line" ]; do
    # Windows line endings
    line=$(printf '%s' "$line" | tr -d '\r')
    # skip blank lines, comments and anything that isn't KEY=value
    printf '%s\n' "$line" | grep -Eq '^[A-Za-z_][A-Za-z0-9_]*=' || continue

    varname=${line%%=*}
    value=$(printenv "$varname" || true)
    [ -n "$value" ] || value=${line#*=}

    printf '  %s: "%s",\n' "$varname" "$(js_escape "$value")"
  done < "$ENV_FILE"
  echo "};"
} > "$tmp_file"

# replace in one step, nginx may already be serving the old file
mv -f "$tmp_file" "$OUTPUT_FILE"
echo "env.sh: wrote $OUTPUT_FILE"