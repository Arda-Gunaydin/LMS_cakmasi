# VPL Lab

Moodle VPL editörünün bilgisayarında çalışan bir kopyası. CSE201 Data Structures (Java) tarzı
ödevleri çözüp **Evaluate**'e bastığında, okuldaki gibi bir not ve rapor alırsın:
`SCORE: 40 / 100`, `8 of 20 checks passed`, RESULTS BY METHOD, WHAT WENT WRONG ve JUnit test ağacı.

Yeni ödev istemek için bilgisayarındaki yapay zekâya (Claude Code, Codex, Cursor, Copilot, Gemini CLI…)
sadece konuyu söylemen yeter: **"LinkedList ödevi hazırla"**. Nasıl hazırlanacağı repo içindeki
talimat dosyalarında yazılı; yapay zekâ ödevi aynı formatta yazar ve kendi kendine test eder.

> Resmi bir araç değildir, hiçbir üniversiteyle bağlantısı yoktur. Kendi pratiğin içindir.
> Okulun gerçek ödev metinlerini, test dosyalarını ya da çözümleri bu repoya koyup paylaşma.

## Kurulum

Gereken tek şey **Java 11 veya üstü (JDK)**. `java -version` ile kontrol edebilirsin.
JDK yoksa: [Adoptium](https://adoptium.net) ya da IntelliJ'de *Project Structure → SDK → Download JDK*.

```bash
git clone <bu-repo-adresi> VPL-Lab
cd VPL-Lab
```

## Başlatma

| Sistem | Nasıl |
|---|---|
| macOS | `start.command` dosyasına çift tıkla (ilk sefer: sağ tık → **Aç**) |
| Windows | `start.bat` dosyasına çift tıkla |
| Terminal | `java VplServer.java` |

Tarayıcıda http://127.0.0.1:8080 kendiliğinden açılır. Kapatmak için terminalde `Ctrl+C`.

Sağ üstte görünen adını değiştirmek için `settings.example.json` dosyasını `settings.json` adıyla
kopyalayıp adını yaz.

## Kullanım

Her ödevde okuldaki gibi **Description**, **Düzenle** ve **Submission view** sekmeleri var.

| Buton | İş |
|---|---|
| ⊞ | Daha fazla göster: yeni dosya, dosya sil |
| 💾 | Kaydet (`Cmd/Ctrl+S`) |
| 🚀 | Run: derler ve `main`'i konsolda çalıştırır; girdiyi konsolun altından yazarsın (`Ctrl+F11` / `Alt+R`) |
| ☑ n | Evaluate: gizli testleri çalıştırır; n, kaç kez değerlendirdiğin (`Shift+F11` / `Alt+E`) |
| 💬 | Sağ paneli aç/kapa: Proposed grade, Compilation (test ağacı), Yorumlar (rapor), Description |
| >_ | Konsol |
| ⤢ | Tam ekran (`Alt+F`, çıkmak için `Esc`) |

Kodun `work/` klasörüne kaydedilir; ödev dosyaları hiç değişmez. Baştan başlamak için
Description sayfasındaki **Reset my code to the starter files**.

## Yeni ödev

> İlk denemen mi? Ufak bir örnekle adım adım anlatan `TUTORIAL.md`'ye bak.

1. Bu klasörü yapay zekâ aracında aç (Claude Code, Codex CLI, Cursor, VS Code + Copilot, Gemini CLI…).
2. Konuyu söyle: `stack ödevi hazırla`, `recursion lab'ı yap`, `hash map postlab'ı hazırla`.
3. Tarayıcıda sayfayı yenile; ödev listede görünür.

Yapay zekâ `AGENTS.md` (ve araca göre `CLAUDE.md`, `GEMINI.md`, `.cursor/rules`,
`.github/copilot-instructions.md`) dosyalarından kuralları okur. Ödevi yazdıktan sonra
`java VplServer.java check <ödev> --solution .scratch/<ödev>` ile doğrular: açıklama formatı,
derlenen başlangıç kodu, testlerin doğru çalışması ve örnek çözümün 100 alması. Geçmeyen ödevi
teslim etmez. Örnek çözümü de sonra siler, çözümü sen istemedikçe göstermez.

Tüm ödevleri kontrol etmek için: `java VplServer.java check --all`

## Klasörler

```
VplServer.java         sunucu + değerlendirici (tek dosya, bağımlılık yok)
lib/T.java             gizli testlerin kullandığı yardımcı (JUnit tarzı rapor)
web/                   arayüz
assignments/<ödev>/    ödevler (format: ASSIGNMENT_FORMAT.md)
work/<ödev>/           senin kodun ve son değerlendirme (git'e gitmez)
```
