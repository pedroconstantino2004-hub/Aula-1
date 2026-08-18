import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class App {
    /** Quantidade máxima de produtos armazenados */
    static final int MAX_NOVOS_PRODUTOS = 10;
    /** Nome do arquivo de dados */
    static String nomeArquivoDados;
    /** Scanner para leitura do teclado */
    static Scanner teclado;
    /** Vetor de produtos */
    static Produto[] produtosCadastrados;
    /** Quantidade de produtos cadastrados */
    static int quantosProdutos = 0;
    /** Pausa o programa */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }
    /** Cabeçalho */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }
    /** Menu principal */
    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar e imprimir os dados de um produto");
        System.out.println("3 - Cadastrar novo produto");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        try {
            return Integer.parseInt(teclado.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    /**
     * Lê os produtos do arquivo.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
        Produto[] produtos =
                new Produto[MAX_NOVOS_PRODUTOS];
        quantosProdutos = 0;
        try (BufferedReader arquivo = new BufferedReader(new FileReader(nomeArquivoDados, Charset.forName("UTF-8")))) {
            String linha = arquivo.readLine();
            if (linha == null) {
                return produtos;
            }
            int quantidade = Integer.parseInt(linha.trim());
            for (int i = 0; i < quantidade && i < MAX_NOVOS_PRODUTOS; i++) {
                linha = arquivo.readLine();
                if (linha == null) {
                    break;
                }
                if (linha.trim().isEmpty()) {
                    continue;
                }
                try {
                    Produto produto = Produto.criarDoTexto(linha);
                    produtos[quantosProdutos] = produto;
                    quantosProdutos++;
                } catch (IllegalArgumentException e) {
                    System.out.println("Erro ao carregar produto: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Arquivo de dados não encontrado.");
        } catch (NumberFormatException e) {
            System.out.println("Quantidade de produtos inválida.");
        }
        return produtos;
    }
    /**
     * Procura um produto pela descrição.
     */
    static void localizarProdutos() {
        System.out.print("Digite a descrição do produto: ");
        String descricao = teclado.nextLine();
        for (int i = 0; i < quantosProdutos; i++) {
            if (produtosCadastrados[i].getDescricao().equalsIgnoreCase(descricao)) {
                System.out.println();
                System.out.println(produtosCadastrados[i]);
                return;
            }
        }
        System.out.println("Produto não encontrado.");
    }
    /**
     * Salva os produtos no arquivo.
     */
    public static void salvarProdutos(
            String nomeArquivo) {
        try (BufferedWriter arquivo = new BufferedWriter(new FileWriter(nomeArquivo, Charset.forName("UTF-8")))) {
            arquivo.write(Integer.toString(quantosProdutos));
            arquivo.newLine();
            for (int i = 0; i < quantosProdutos; i++) {
                arquivo.write(produtosCadastrados[i].gerarDadosTexto());
                arquivo.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }
    /**
     * Lista todos os produtos.
     */
    static void listarTodosOsProdutos() {
        if (quantosProdutos == 0) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }
        for (int i = 0; i < quantosProdutos; i++) {
            System.out.println((i + 1) + " - " + produtosCadastrados[i]);
            System.out.println();
        }
    }
    /**
     * Cadastra um novo produto.
     */
    static void cadastrarProduto() {
        if (quantosProdutos >= MAX_NOVOS_PRODUTOS) {
            System.out.println("Limite máximo de produtos atingido.");
            return;
        }
        try {
            System.out.println();
            System.out.println("TIPO DE PRODUTO");
            System.out.println("1 - Produto não perecível");
            System.out.println("2 - Produto perecível");
            System.out.print("Digite o tipo: ");
            int tipo = Integer.parseInt(teclado.nextLine());
            if (tipo != 1 && tipo != 2) {
                System.out.println("Tipo de produto inválido.");
                return;
            }
            System.out.print("Descrição: ");
            String descricao = teclado.nextLine();
            System.out.print("Preço de custo: ");
            double precoCusto = Double.parseDouble(teclado.nextLine().replace(',', '.'));
            System.out.print("Margem de lucro " + "(pressione Enter para usar 20%): ");
            String margemTexto = teclado.nextLine().trim();
            Produto produto;
            if (tipo == 1) {
                /*
                 * Produto não perecível.
                 */
                if (margemTexto.isEmpty()) {
                    produto = new ProdutoNaoPerecivel(descricao, precoCusto);
                } else {
                    double margemLucro = Double.parseDouble(margemTexto.replace(',', '.'));
                    produto =new ProdutoNaoPerecivel(descricao, precoCusto, margemLucro);
                }
            } else {
                /*
                 * Produto perecível.
                 */
                System.out.print("Data de validade (dd/MM/yyyy): ");
                String data = teclado.nextLine();
                LocalDate validade = LocalDate.parse(data, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                if (margemTexto.isEmpty()) {
                    produto =new ProdutoPerecivel(descricao, precoCusto, validade);
                } else {
                    double margemLucro = Double.parseDouble(margemTexto.replace(',', '.'));
                    produto =new ProdutoPerecivel(descricao, precoCusto, margemLucro, validade);
                }
            }
            /*
             * Polimorfismo:
             * o vetor é de Produto, mas pode receber
             * ProdutoNaoPerecivel ou ProdutoPerecivel.
             */
            produtosCadastrados[quantosProdutos] = produto;
            quantosProdutos++;
            System.out.println();
            System.out.println("Produto cadastrado com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("Valor numérico inválido.");
        } catch (DateTimeParseException e) {
            System.out.println("Data inválida. " + "Use o formato dd/MM/yyyy.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        teclado =new Scanner(System.in, Charset.forName("UTF-8"));
        nomeArquivoDados = "dadosProdutos.csv";
        produtosCadastrados = lerProdutos(nomeArquivoDados);
        int opcao;
        do {
            opcao = menu();
            switch (opcao) {
                case 1:
                    listarTodosOsProdutos();
                    pausa();
                    break;
                case 2:
                    localizarProdutos();
                    pausa();
                    break;
                case 3:
                    cadastrarProduto();
                    pausa();
                    break;
                case 0:
                    System.out.println("Encerrando o programa...");
                    break;
                default:
                    System.out.println("Opção inválida.");
                    pausa();
                    break;
            }
        } while (opcao != 0);
        salvarProdutos(nomeArquivoDados);
        teclado.close();
    }
}
