## Minigame - Zeldaminiclone

Pequeno jogo Java (clone/simplificação) com movimentação, inimigos e áudio.

### Descrição

Este repositório contém um minigame em Java com a janela de jogo, sistema de câmera, inimigos e efeitos sonoros. O código usa o módulo `zeldaminiclone` (veja `module-info.java`) e carrega assets de áudio a partir de `/res/audio` (embutidos no classpath em `res/audio` / `bin/audio`).

### Como executar

Recomendo abrir o projeto em uma IDE (IntelliJ IDEA, Eclipse) e executar a classe `zeldaminiclone.Game` (método `main`).

Se preferir linha de comando (PowerShell), abaixo seguem instruções básicas para compilação e execução a partir da raiz do projeto (`caminho/para/zeldaminiclone`):

1. Compilar as fontes (cria a pasta `out`):

```powershell
javac -d out src\module-info.java src\zeldaminiclone\zeldaminiclone\*.java
```

2. Executar o módulo:

```powershell
java --module-path out -m zeldaminiclone/zeldaminiclone.Game
```

Observações:

- A compilação acima assume que todos os arquivos `.java` estão em `src\zeldaminiclone\zeldaminiclone` e que há um `module-info.java` em `src`.
- Pode ser necessário ajustar caminhos conforme sua IDE ou estrutura local.
- Se preferir usar o classpath tradicional, você também pode compilar com `javac -d out src\zeldaminiclone\zeldaminiclone\*.java` e rodar com `java -cp out zeldaminiclone.Game` (mas com `module-info.java` presente, a execução por módulo é a abordagem recomendada).

### Arquivos de áudio encontrados

Os arquivos de áudio inclusos no projeto (localizados em `res/audio` e copiados para `bin/audio`) são:

- `enemyDeath.wav` (som da morte do inimigo)
- `enemyShoot.wav` (som da bola de fogo do inimigo)
- `gameOverLost.wav` (som de derrota)
- `gameOverWin.wav` (som de vitória)
- `gameplayMusic.wav` (música de gameplay)
- `menuMusic.wav` (música do menu)
- `playerAttack.wav` (som da bola de fogo do player)

No código, os caminhos usados pelo `AudioManager` são strings como `/audio/gameplayMusic.wav` e `/audio/gameOverLost.wav` (veja `src/zeldaminiclone/AudioManager.java`).

### Créditos de áudio

As músicas/efeitos principais usados no jogo têm as seguintes fontes (fornecidas pelo autor deste repositório):

- Gameplay Music (arquivo: `gameplayMusic.wav`)

  - Autor: Mrthenoronha
  - Fonte: https://freesound.org/people/Mrthenoronha/sounds/520937/

- Game Over (derrota) (arquivo: `gameOverLost.wav`)

  - Autor: LilMati
  - Fonte: https://freesound.org/people/LilMati/sounds/435194/

- Game Win (vitória) (arquivo: `gameOverWin.wav`)
  - Autor: EVRetro
  - Fonte: https://freesound.org/people/EVRetro/sounds/533034/

### Estrutura relevante do projeto

- `src/` - código fonte Java
- `res/audio/` - assets de áudio (arquivos .wav)
- `bin/audio/` - cópia/artefato de áudio usado em builds locais

### Contato / Créditos do projeto

Projeto criado por Gabriel Couto.
