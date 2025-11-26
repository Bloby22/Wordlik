# 🎮 Wordlik - České Wordle pro Minecraft

Wordlik je komplexní Minecraft plugin, který přináší oblíbenou slovní hru 
Wordle přímo na váš server! Hráči mohou hádat 5písmenná česká slova 
s intuitivním barevným feedbackem a sledovat své pokroky.

## ✨ Klíčové funkce

### 🎯 Herní mechanika
- **5písmenná česká slova** - Stovky slov v databázi
- **6 pokusů** na uhodnutí správného slova
- **Barevný feedback**:
  - 🟩 Zelená = písmeno na správném místě
  - 🟨 Žlutá = písmeno je ve slově, ale jinde
  - ⬛ Šedá = písmeno není ve slově

### 📊 Statistiky a pokrok
- Celkový počet her a výher
- Procento úspěšnosti
- Aktuální a nejlepší série výher
- Průměrný počet pokusů
- Distribuce pokusů (1-6)
- Historie hraných slov

### 🎨 Uživatelské rozhraní
- Čisté a přehledné zprávy v chatu
- Barevně zvýrazněné písmena
- ASCII art pro výhry/prohry
- Vizuální grafy statistik

### 🔧 Administrace
- Konfigurovatelné přes config.yml
- Zvuky lze zapnout/vypnout
- Debug režim pro vývojáře
- Automatické ukládání dat
- Reload příkaz bez restartu

## 📋 Příkazy

- `/wordlik start` - Začít novou hru
- `/wordlik <slovo>` - Hádat slovo
- `/wordlik hint` - Získat nápovědu (první písmeno)
- `/wordlik stop` - Ukončit aktuální hru
- `/wordlik stats` - Zobrazit své statistiky
- `/wordlik help` - Zobrazit nápovědu
- `/wordlik reload` - Reload konfigurace (admin)

**Aliasy:** `/wl`, `/wordle`

## 🎮 Jak hrát

1. Spusťte hru pomocí `/wordlik start`
2. Hádejte 5písmenná česká slova pomocí `/wordlik <slovo>`
3. Sledujte barevný feedback pro každé písmeno
4. Máte 6 pokusů na uhodnutí správného slova
5. Sledujte své statistiky pomocí `/wordlik stats`

## ⚙️ Konfigurace
```yaml
sounds:
  enabled: true          # Zapnout/vypnout zvuky
debug-mode: false        # Debug režim
auto-save-interval: 300  # Interval auto-save (sekundy)
```

## 🔒 Oprávnění

- `wordlik.use` - Základní používání pluginu (výchozí: všichni)
- `wordlik.admin` - Admin příkazy (výchozí: OP)

## 📦 Instalace

1. Stáhněte `wordlik.jar`
2. Umístěte do složky `plugins/`
3. Restartujte server
4. Upravte `config.yml` podle potřeby
5. Užijte si hru!

## 💡 Použití

Ideální pro:
- 🏰 Lobby servery
- 🎪 Mini-game servery
- ⏰ Čekání mezi hrami
- 🎓 Vzdělávací servery
- 👥 Komunitní servery

## 🐛 Podpora

Verze: 1.0.0
API: Spigot/Paper 1.20+
Java: 17+

Pro bug reporty a návrhy navštivte naši GitHub stránku.
