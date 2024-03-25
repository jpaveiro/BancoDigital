package com.tr.api.bluebank.util;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tr.api.bluebank.model.Cliente;
import com.tr.api.bluebank.repository.ClienteRepository;

public class Utils {
	
    private ClienteRepository clienteRepository;

    public Utils(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }
	
    public boolean validarCPF(String cpf) {
        // Verifica o formato do CPF
        if (cpf == null || !cpf.matches("\\d{11}")) {
            return false;
        }

        // Verifica se o CPF já existe
        ClienteRepository repository = new ClienteRepository();
        if (repository.cpfExists(cpf)) {
            return false;
        }

        return true;
    }
    
    public boolean validarNome(String nome) {
        // Verifica se o nome está dentro do limite de caracteres e não contém números
        return nome != null && nome.length() >= 2 && nome.length() <= 100 && nome.matches("^[a-zA-Z\\s'-]+$");
    }
    
    public boolean validarDataNascimento(LocalDate dataNascimento) {
        LocalDate hoje = LocalDate.now();
        Period periodo = Period.between(dataNascimento, hoje);
        return periodo.getYears() >= 18;
    }
    
    public void definirStatusCliente(Cliente cliente) {
        Random rand = new Random();
        int sorteio = rand.nextInt(1001); // Gera um número entre 0 e 1000

        if (sorteio <= 333) {
            cliente.setStatus("COMUM");
        } else if (sorteio <= 666) {
            cliente.setStatus("SUPER");
        } else {
            cliente.setStatus("PREMIUM");
        }
    }
    
    public boolean validarCep(String cep) {
    	cep = cep.replace(".", "").replace("-", "");  // Limpa a formatação do CPF, se houver
        
        // Verifica o formato do Cep
        if (cep == null || !cep.matches("\\d{8}")) {
            return false;
        }
        
        return true;
    }
    
    public void gerarNumeroConta(Cliente cliente) {
        Random random = new Random();
        int numeroAleatorio = random.nextInt(90000000) + 10000000; // Garante 8 dígitos
        cliente.setNumeroConta(numeroAleatorio);
    }
    
    public void gerarIdUnico(Cliente cliente) {
        List<Cliente> clientes = clienteRepository.findAll();
        if (clientes != null && !clientes.isEmpty()) {
            // Encontra o maior ID atual
            Optional<Long> maxId = clientes.stream()
                    .map(Cliente::getId)
                    .max(Long::compare);
            // Incrementa o maior ID encontrado e atribui ao cliente
            cliente.setId(maxId.orElse(0L) + 1);
        } else {
            // Se não houver clientes, define o ID como 1
            cliente.setId(1L);
        }
    }
    
    public static String formatarSaldoParaExibicao(double saldo) {
        Locale ptBr = new Locale("pt", "BR");
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(ptBr);
        return formatoMoeda.format(saldo);
    }
    
    public static String removerNaoNumericos(String input) {
        // Remove todos os caracteres que não são dígitos nem o ponto decimal, se tiver ele deixa o ponto decimal
        Pattern pattern = Pattern.compile("[^\\d,]");
        Matcher matcher = pattern.matcher(input);
        String result = matcher.replaceAll("");
        
        // Substitui a vírgula por ponto, se presente
        result = result.replace(",", ".");
        
        return result;
    } 
}
