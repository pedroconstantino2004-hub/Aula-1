import java.text.NumberFormat;

public abstract class Produto {
     private static final double MARGEM_PADRAO = 0.2;
     protected String descricao;
     protected double precoCusto;
     protected double margemLucro;
     /**
     * Inicializa os atributos do produto.
     *
     * @param desc descrição do produto (mínimo de 3 caracteres)
     * @param precoCusto preço de custo (maior que zero)
     * @param margemLucro margem de lucro (maior que zero)
     */
     private void init(String desc, double precoCusto, double margemLucro) {
          if (desc == null || desc.length() < 3) {
               throw new IllegalArgumentException("A descrição deve possuir pelo menos 3 caracteres.");
          }
          if (precoCusto <= 0.0) {
               throw new IllegalArgumentException("O preço de custo deve ser maior que zero.");
          }
          if (margemLucro <= 0.0) {
               throw new IllegalArgumentException("A margem de lucro deve ser maior que zero.");
          }
          this.descricao = desc;
          this.precoCusto = precoCusto;
          this.margemLucro = margemLucro;
     }
     /**
     * Construtor com margem de lucro informada.
     */
     protected Produto(String desc, double precoCusto, double margemLucro) {
          init(desc, precoCusto, margemLucro);
     }
     /**
     * Construtor utilizando a margem padrão de 20%.
     */
     protected Produto(String desc,double precoCusto) {
          init(desc, precoCusto, MARGEM_PADRAO);
     }
     /**
     * Retorna o valor de venda do produto.
     *
     * Cada tipo de produto possui sua própria implementação.
     */
     public abstract double valorDeVenda();
     /**
     * Retorna os dados básicos do produto.
     */
     @Override
     public String toString() {
          NumberFormat moeda =
          NumberFormat.getCurrencyInstance();
          return "NOME: " + descricao + ": " + moeda.format(valorDeVenda());
     }
     /**
     * Dois produtos são considerados iguais quando possuem
     * a mesma descrição, independentemente de maiúsculas/minúsculas.
     */
     @Override
     public boolean equals(Object obj) {
     if (this == obj) {
          return true;
          }
          if (!(obj instanceof Produto)) {
          return false;
          }
          Produto outro = (Produto) obj;
          return descricao.equalsIgnoreCase(outro.descricao);
     }
     /**
     * Cria um produto a partir de uma linha de texto.
     *
     * Produto não perecível:
     * 1;descricao;precoCusto;margemLucro
     *
     * Produto perecível:
     * 2;descricao;precoCusto;margemLucro;dataValidade
     */
     static Produto criarDoTexto(String linha) {
          String[] dados = linha.split(";");
          if (dados.length < 4) {
               throw new IllegalArgumentException("Dados insuficientes para criar o produto.");
          }
          int tipo = Integer.parseInt(dados[0].trim());
          String descricao = dados[1].trim();
          double precoCusto = Double.parseDouble(dados[2].trim().replace(',', '.'));
          double margemLucro = Double.parseDouble(dados[3].trim().replace(',', '.'));
          if (tipo == 1) {
               return new ProdutoNaoPerecivel(descricao, precoCusto, margemLucro);
          } else if (tipo == 2) {
               if (dados.length < 5) {
                    throw new IllegalArgumentException("Produto perecível sem data de validade.");
               }
               java.time.LocalDate validade = java.time.LocalDate.parse(dados[4].trim(), java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
               return new ProdutoPerecivel(descricao, precoCusto, margemLucro, validade);
          }
          throw new IllegalArgumentException("Tipo de produto inválido.");
     }
     /**
     * Gera os dados do produto em formato texto.
     */
     public abstract String gerarDadosTexto();
}
