## Minigame - Zeldaminiclone

Pequeno jogo Java (clone/simplificação) com movimentação, inimigos e áudio.

### Descrição

Este repositório contém um minigame em Java com a janela de jogo, sistema de câmera, inimigos e efeitos sonoros. O código usa o módulo `zeldaminiclone` (veja `module-info.java`) e carrega assets de áudio a partir de `/res/audio` (embutidos no classpath em `res/audio` / `bin/audio`).

### Requisitos do Sistema

- **Java 11 ou superior** (recomendado Java 23+)
- **Sistema Operacional:** Windows, macOS ou Linux
- **RAM:** 512 MB mínimo
- **Áudio:** placa de som (opcional, mas recomendado para uma melhor experiência)

### Como executar

#### Opção 1: Executável JAR (Recomendado)

Execute o arquivo `Game.jar` diretamente:

```powershell
java -jar Game.jar
```

**O que você precisa:**

- Java 11+ instalado (testado com Java 23)
- O arquivo `Game.jar` na raiz do projeto

**Observação:** O JAR contém todos os arquivos de áudio embutidos, então não precisa de nenhuma configuração adicional. Basta clicar ou executar o comando acima.

#### IDE (IntelliJ IDEA, Eclipse)

Abra o projeto em uma IDE e execute a classe `zeldaminiclone.Game` (método `main`).

#### Linha de comando - Compilação Manual

Se preferir compilar e rodar a partir da raiz do projeto:

1. Compilar as fontes (cria a pasta `out`):

```powershell
javac -d out src\module-info.java src\zeldaminiclone\*.java
```

2. Executar o módulo:

```powershell
java --module-path out -m zeldaminiclone/zeldaminiclone.Game
```

**Observações:**

- Certifique-se de que Java está no PATH do sistema
- A compilação assume que os arquivos `.java` estão em `src\zeldaminiclone\`
- Para usar classpath tradicional: `javac -d out src\zeldaminiclone\*.java` e rodar com `java -cp out zeldaminiclone.Game`

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

### Controles do jogo

- **W** - Mover para cima
- **A** - Mover para esquerda
- **S** - Mover para baixo
- **D** - Mover para direita
- **Mouse (Clique esquerdo)** - Atirar
- **Alt + Enter** - Ativar/desativar modo fullscreen
- **R** - Reiniciar (após game over)
- **Esc** - Voltar ao menu (após game over)

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
