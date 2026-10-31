# Simulador de Algoritmos de Substituição de Páginas

Simulador em Java dos algoritmos FIFO, LRU, Relógio e Ótimo.

## Requisitos

- Java (JDK) 17 ou superior
- Biblioteca JFreeChart (já incluída em `lib/jfreechart-1.5.6.jar`)

## Como executar

Todos os comandos devem ser executados na pasta raiz do projeto.

### 1. Compilar

```bash
javac -cp lib/jfreechart-1.5.6.jar *.java
```

### 2. Rodar a simulação

```bash
java -cp .:lib/jfreechart-1.5.6.jar Main
```

O programa pergunta no terminal:

1. A sequência de páginas, separadas por espaço (ex: `7 0 1 2 0 3`). Enter vazio usa uma sequência de exemplo.
2. O número de molduras (ex: `3`).

Depois mostra as faltas de página de cada algoritmo e gera os arquivos:

- `resultados.csv`: total de faltas por algoritmo
- `log_execucao.csv`: passo a passo de cada simulação
- `grafico_faltas.png`: gráfico de colunas comparando os algoritmos

### 3. Abrir a interface gráfica

```bash
java -cp .:lib/jfreechart-1.5.6.jar InterfaceGrafica
```

Clique em **Carregar resultados do CSV** para ver a tabela e os gráficos.
A interface lê o `log_execucao.csv`, então rode o passo 2 antes.

> No Windows, troque `:` por `;` no `-cp` (ex.: `-cp .;lib/jfreechart-1.5.6.jar`).
