#!/bin/bash
set -e

if [[ -n "$VALIDATION_KEY" && -n "$BASE_URL" ]]; then
    mkdir -p /root/.minecraft/monumenta-scraper/config
    echo "{\"validationKey\":\"$VALIDATION_KEY\", \"baseUrl\":\"$BASE_URL\"}" > /root/.minecraft/monumenta-scraper/config/api.json
else
    echo "env error"
    exit 1
fi

if [ "$1" = "--login" ]; then
    exec java -jar headlessmc-launcher.jar --command login
fi

mkdir -p /root/.minecraft/mods

cp -f /minecraft/mods-cache/*.jar /root/.minecraft/mods/

touch /root/.minecraft/options.txt
cat <<EOF >> /root/.minecraft/options.txt
onboardAccessibility:false
pauseOnLostFocus:false
EOF

if [ $# -eq 0 ]; then
    exec java -jar headlessmc-launcher.jar --command launch fabric:1.20.4 -lwjgl
fi

exec "$@"