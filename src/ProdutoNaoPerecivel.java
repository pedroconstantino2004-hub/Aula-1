import java.util.Locale;

public class ProdutoNaoPerecivel extends Produto {
     /**
     * Construtor com margem de lucro informada.
     */
     public ProdutoNaoPerecivel(String desc, double precoCusto, double margemLucro) {
          super(desc, precoCusto, margemLucro);
     }
     /**
     * Construtor com margem de lucro padrão de 20%.
     */
     public ProdutoNaoPerecivel(String desc, double precoCusto) {
          super(desc, precoCusto);
     }
     /**
     * Calcula o preço de venda.
     *
     * preço de venda = preço de custo + margem de lucro
     */
     @Override
     public double valorDeVenda() {
          return precoCusto * (1.0 + margemLucro);
     }
     /**
     * Gera os dados do produto para armazenamento.
     */
     @Override
     public String gerarDadosTexto() {
          return String.format(Locale.US, "1;%s;%.2f;%.2f", descricao, precoCusto, margemLucro);
     }
}
