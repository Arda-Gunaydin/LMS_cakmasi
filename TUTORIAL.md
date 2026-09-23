# Adım Adım: İlk Ödevini Hazırla

Bu, sıfırdan başlayıp ufak bir örnek ödevle tüm akışı denemen için kısa bir rehberdir.
Kurulum detayları için `README.md`'ye bak; burada sadece "ilk kullanım" anlatılıyor.

## 1. Programı aç

| Sistem | Nasıl |
|---|---|
| macOS | `start.command` dosyasına çift tıkla (ilk sefer: sağ tık → **Aç**) |
| Windows | `start.bat` dosyasına çift tıkla |
| Terminal | `java VplServer.java` |

Tarayıcıda http://127.0.0.1:8080 kendiliğinden açılır. Şu an ödev listesi muhtemelen boş
ya da sadece örnekleri gösteriyor — normal, ilk ödevini birazdan sen oluşturacaksın.

## 2. Adını ayarla (isteğe bağlı)

Sağ üstte adının görünmesi için `settings.example.json` dosyasını `settings.json` olarak
kopyala ve içindeki `"userName"` alanına adını yaz. Sunucuyu yeniden başlatman gerekmez,
sayfayı yenilemen yeterli.

## 3. Küçük bir ödev iste

Bu klasörü bir yapay zekâ aracında aç (Claude Code, Cursor, GitHub Copilot, Gemini CLI, ne
kullanıyorsan). İlk denemen için gerçekten ufak bir konu seç, mesela:

> **"toplama ödevi hazırla"**

ya da

> **"iki sayının ortalamasını bulan çok basit bir lab yap"**

Tek cümle yeterli. Nasıl bir ödev olacağını, kaç kontrol içereceğini, nasıl
değerlendirileceğini **sorma** — bunların hepsi `AGENTS.md` içinde tanımlı ve yapay zekâ
onu otomatik okuyor. Sen sadece konuyu söylüyorsun.

## 4. Yapay zekâ arka planda ne yapıyor?

Sen bunu görmüyorsun (ekranda talimat metni açılmıyor), ama arka planda şu adımlar
çalışıyor:

1. `assignments/<id>/` altında yeni bir klasör açıyor: `assignment.json`, okul formatında
   bir `description.html`, öğrencinin dolduracağı `starter/` dosyası ve gizli
   `tests/Tests.java`.
2. Kendi kendine bir örnek çözüm yazıp `java VplServer.java check <id> --solution ...`
   komutuyla doğruluyor: açıklama formatı doğru mu, boş şablon düşük not alıyor mu,
   doğru çözüm 100 alıyor mu.
3. Geçmeyen bir ödevi sana teslim etmiyor; hatayı kendi düzeltip tekrar dener.
4. Doğrulama bitince örnek çözümü siliyor — çözümü sana göstermiyor.

Bu yüzden ilk denemende ufak bir konu seçmek iyi bir fikir: doğrulama döngüsü hızlı biter
ve akışın nasıl işlediğini görürsün.

## 5. Ödevi aç ve çöz

Tarayıcıda sayfayı yenile (F5) — yeni ödev listede görünür. Üstüne tıkla:

1. **Description** sekmesinde ödev metnini oku.
2. **Düzenle** sekmesinde koda geç, `starter/` dosyasını doldur, 💾 ile kaydet
   (`Cmd/Ctrl+S`).
3. 🚀 **Run** ile hızlıca dene, çıktıyı gör.
4. ☑ **Evaluate** ile gizli testleri çalıştır — okuldaki gibi bir not
   (`SCORE: x / 100`) ve rapor alırsın.

Rapor tam istediğin gibi değilse ya da bir kontrol yanlış görünüyorsa, yapay zekâya
söyle ("testler yanlış", "daha kolay yap" gibi) — yine tek cümle yeterli.

## 6. Sıradaki ödev

Aynı akışı istediğin her konu için tekrarla: "linked list ödevi hazırla",
"recursion postlab'ı yap" vb. Her ödev kendi klasöründe durur, birbirini etkilemez.
