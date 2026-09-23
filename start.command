#!/bin/bash
# VPL Lab - macOS'ta cift tiklayarak baslatir.
cd "$(dirname "$0")" || exit 1

find_java() {
  # 1) PATH'teki java (macOS'un sahte /usr/bin/java'si JDK yoksa hata verir)
  if command -v java >/dev/null 2>&1 && java -version >/dev/null 2>&1; then
    echo "java"; return
  fi
  # 2) sistemde kayitli JDK
  if [ -x /usr/libexec/java_home ]; then
    local h
    h=$(/usr/libexec/java_home 2>/dev/null)
    if [ -n "$h" ] && [ -x "$h/bin/java" ]; then echo "$h/bin/java"; return; fi
  fi
  # 3) IntelliJ'nin indirdigi JDK'lar
  local j
  j=$(ls -d "$HOME"/Library/Java/JavaVirtualMachines/*/Contents/Home/bin/java 2>/dev/null | sort -V | tail -1)
  if [ -n "$j" ]; then echo "$j"; return; fi
  echo ""
}

JAVA=$(find_java)
if [ -z "$JAVA" ]; then
  echo "Java (JDK 11 veya ustu) bulunamadi."
  echo "IntelliJ'de File > Project Structure > SDK > Download JDK ile indirebilir"
  echo "ya da https://adoptium.net adresinden kurabilirsin."
  read -r -p "Kapatmak icin Enter..."
  exit 1
fi

echo "Java: $JAVA"
exec "$JAVA" VplServer.java
