package com.tr.api.bluebank.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import com.tr.api.bluebank.enums.TipoApoliceEnum;
import com.tr.api.bluebank.model.ApoliceSeguro;
import com.tr.api.bluebank.model.Cartao;
import com.tr.api.bluebank.model.Cliente;
import com.tr.api.bluebank.model.SeguroCartao;
import com.tr.api.bluebank.repository.ClienteRepository;
import com.tr.api.bluebank.util.Utils;

public class ClienteService {

	private ClienteRepository clienteRepository;
	private Utils utils;

	public ClienteService(ClienteRepository clienteRepository, Utils utils) {
		this.clienteRepository = clienteRepository;
		this.utils = utils;
	}

	public void cadastrarCliente(Scanner scanner, ClienteService clienteService, DateTimeFormatter formatter) {
		System.out.println("\u001B[32m" + "CADASTRO DE CLIENTE" + "\u001B[0m");

		String nome;
		while (true) {
			System.out.println("\u001B[36m" + "\nNome: " + "\u001B[0m");
			nome = scanner.nextLine();
			if (utils.validarNome(nome))
				break;
			System.out.println(
					"\u001B[31m" + "O nome deve ter entre 2 e 100 caracteres, e não pode ter números nem acentos." + "\u001B[0m");
		}

		String cpf;
		while (true) {
			System.out.println("\u001B[36m" + "\nCPF: " + "\u001B[0m");
			cpf = scanner.nextLine();
			cpf = cpf.replace(".", "").replace("-", ""); // Limpa a formatação do CPF, se houver

			// Verifica o formato e a unicidade do CPF
			if (!cpf.matches("\\d{11}")) {
				System.out.println("\u001B[31m" + "CPF inválido! O CPF deve ter 11 dígitos numéricos." + "\u001B[0m");
			} else if (utils.validarCPF(cpf)) {
				break; // CPF é válido e não está cadastrado, sai do loop
			} else {
				System.out.println("\u001B[31m" + "\nCPF já cadastrado no sistema." + "\u001B[0m");
			}
		}

		LocalDate dataNascimento = null;
		while (dataNascimento == null) {
			System.out.println("\u001B[36m" + "\nData de Nascimento (dd-MM-yyyy): " + "\u001B[0m");
			try {
				dataNascimento = LocalDate.parse(scanner.nextLine(), formatter);
				if (!utils.validarDataNascimento(dataNascimento)) {
					System.out.println("\u001B[33m" + "\nCliente menor de 18 anos. Digite 'novo' para cadastrar novo cliente ou pressione 'ENTER' para corrigir a data." + "\u001B[0m");

					while (true) {
						String input = scanner.nextLine();
						if (input.equalsIgnoreCase("novo")) {
							cadastrarCliente(scanner, clienteService, formatter);
							return; // Sai do método atual após iniciar o cadastro de um novo cliente
						} else if (input.isEmpty()) {
							break; // Sai do loop para corrija a data
						} else {
							System.out.println("\u001B[31m" + "\nOpção inválida. Digite 'novo' para cadastrar novo cliente ou 'enter' para corrigir a data." + "\u001B[0m");
						}
					}

					dataNascimento = null; 
				}
			} catch (DateTimeParseException e) {
				System.out.println("\u001B[31m" + "Formato de data inválido." + "\u001B[0m");
			}
		}

		System.out.println("\u001B[36m" + "\nRua: " + "\u001B[0m");
		String rua = scanner.nextLine();

		System.out.println("\u001B[36m" + "\nNúmero: " + "\u001B[0m");
		String numero = scanner.nextLine();

		String cep;
		while (true) {
			System.out.println("\u001B[36m" + "\nCEP: " + "\u001B[0m");
			cep = scanner.nextLine();

			// Verifica apenas o formato do CEP
			if (!utils.validarCep(cep)) {
				System.out.println("\u001B[31m" + "\nCEP inválido! O CEP deve ter 8 dígitos numéricos." + "\u001B[0m");
			} else {
				break; // Se o CEP é válido, sai do loop
			}
		}

		System.out.println("\u001B[36m" + "\nCidade: " + "\u001B[0m");
		String cidade = scanner.nextLine();

		System.out.println("\u001B[36m" + "\nEstado: " + "\u001B[0m");
		String estado = scanner.nextLine();

		System.out.println(
				"\u001B[36m" + "\nTipo de Conta (Digite 'C' para Conta Corrente ou 'P' para Poupança): " + "\u001B[0m");
		String tipoConta = scanner.nextLine().toUpperCase();

		while (!tipoConta.equals("C") && !tipoConta.equals("P")) {
			System.out.println("\u001B[31m" + "Tipo de conta inválido! Digite 'C' para Conta Corrente ou 'P' para Poupança." + "\u001B[0m");
			tipoConta = scanner.nextLine().toUpperCase();
		}

		Cliente cliente = new Cliente();
		cliente.setCpf(cpf);
		cliente.setNome(nome);
		cliente.setDataNascimento(dataNascimento);
		cliente.setRua(rua);
		cliente.setNumero(numero);
		cliente.setCep(cep);
		cliente.setCidade(cidade);
		cliente.setEstado(estado);
		cliente.setTipoConta(tipoConta);

		System.out.println("\u001B[36m" + "\nVerificando rating por favor aguarde... " + "\u001B[0m");

		try {
			Thread.sleep(5000); // Pausa de 5 segundos
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // Restaura o estado de interrupção
			System.out.println("Houve uma interrupção na consulta do rating.");
		}
		utils.definirStatusCliente(cliente);
		utils.gerarNumeroConta(cliente);
		utils.gerarIdUnico(cliente);
		clienteRepository.save(cliente);

		System.out.println("\u001B[36m" + "\nCliente do tipo = " + "\u001B[0m" + cliente.getStatus());
		System.out.println("\u001B[36m" + "\nNúmero da conta = " + "\u001B[0m" + cliente.getNumeroConta());
		System.out.println("\u001B[32m" + "\nCliente salvo com sucesso!!" + "\u001B[0m");


	}

	public void deposito(Scanner scanner) {
		System.out.println("\u001B[36m" + "\nDepósito" + "\u001B[0m");

		// Solicita número da conta
		System.out.println("\u001B[36m" + "\nDigite o número da conta: " + "\u001B[0m");
		String numeroContaStr = scanner.nextLine();
		String numeroContaFormatado = Utils.removerNaoNumericos(numeroContaStr);
		int numeroConta = Integer.parseInt(numeroContaFormatado);

		// Verifica se a conta existe
		Cliente cliente = clienteRepository.findByNumeroConta(numeroConta);
		if (cliente == null) {
			System.out.println("\u001B[31m" + "\nConta não encontrada." + "\u001B[0m");
			return; // Sai do método se a conta não for encontrada
		}

		// Solicita valor do depósito
		System.out.println("\u001B[36m" + "\nDigite o valor a ser depositado: " + "\u001B[0m");
		String valorDepositoStr = scanner.nextLine();
		String valorDepositoFormatado = Utils.removerNaoNumericos(valorDepositoStr);
		double valorDeposito = Double.parseDouble(valorDepositoFormatado);

		// Confirma e efetua depósito
		System.out.println("\u001B[36m" + "\nConfirma o depósito de " + Utils.formatarSaldoParaExibicao(valorDeposito)
				+ " na conta " + numeroConta + "? (sim/não)" + "\u001B[0m");
		String confirmacao = scanner.nextLine();

		if (confirmacao.equalsIgnoreCase("sim")) {
			// Efetua o depósito
			cliente.setSaldo(cliente.getSaldo() + valorDeposito);
			clienteRepository.update(cliente);
			System.out.println("\u001B[32m" + "\nDepósito realizado com sucesso!" + "\u001B[0m");
		} else if (confirmacao.equalsIgnoreCase("não")) {
			System.out.println("\u001B[33m" + "\nOperação de depósito cancelada." + "\u001B[0m");
		} else {
			System.out.println("\u001B[31m" + "\nOpção inválida." + "\u001B[0m");
		}
	}

	public void saque(Scanner scanner) {
		System.out.println("\u001B[36m" + "\nSaque" + "\u001B[0m");

		// Solicita número da conta
		System.out.println("\u001B[36m" + "\nDigite o número da conta: " + "\u001B[0m");
		String numeroContaStr = scanner.nextLine();
		String numeroContaFormatado = Utils.removerNaoNumericos(numeroContaStr);
		int numeroConta = Integer.parseInt(numeroContaFormatado);

		// Verifica se a conta existe
		Cliente cliente = clienteRepository.findByNumeroConta(numeroConta);
		if (cliente == null) {
			System.out.println("\u001B[31m" + "\nConta não encontrada." + "\u001B[0m");
			return; // Sai do método se a conta não for encontrada
		}

		// Solicita valor do saque
		System.out.println("\u001B[36m" + "\nDigite o valor a ser sacado: " + "\u001B[0m");
		String valorSaqueStr = scanner.nextLine();
		String valorSaqueFormatado = Utils.removerNaoNumericos(valorSaqueStr);
		double valorSaque = Double.parseDouble(valorSaqueFormatado);

		// Verifica se o saldo é suficiente para o saque
		if (cliente.getSaldo() < valorSaque) {
			System.out.println("\u001B[31m" + "\nSaldo insuficiente para realizar o saque." + "\u001B[0m");
			return; // Sai do método se o saldo for insuficiente
		}

		// Confirma e efetua o saque
		System.out.println("\u001B[36m" + "\nConfirma o saque de " + Utils.formatarSaldoParaExibicao(valorSaque)
				+ " da conta " + numeroConta + "? (sim/não)" + "\u001B[0m");
		String confirmacao = scanner.nextLine();

		if (confirmacao.equalsIgnoreCase("sim")) {
			// Efetuar o saque
			cliente.setSaldo(cliente.getSaldo() - valorSaque);
			clienteRepository.update(cliente);
			System.out.println("\u001B[32m" + "\nSaque realizado com sucesso!" + "\u001B[0m");
		} else if (confirmacao.equalsIgnoreCase("não")) {
			System.out.println("\u001B[33m" + "\nOperação de saque cancelada." + "\u001B[0m");
		} else {
			System.out.println("\u001B[31m" + "\nOpção inválida." + "\u001B[0m");
		}
	}

	public void realizarPix(Scanner scanner) {
		System.out.println("\u001B[36m" + "\nTransferência via PIX" + "\u001B[0m");

		// Solicita número da conta de origem
		System.out.println("\u001B[36m" + "\nDigite o número da conta de origem: " + "\u001B[0m");
		String numeroContaOrigemStr = scanner.nextLine();
		String numeroContaOrigemFormatado = Utils.removerNaoNumericos(numeroContaOrigemStr);
		int numeroContaOrigem = Integer.parseInt(numeroContaOrigemFormatado);

		// Verifica se a conta de origem existe
		Cliente clienteOrigem = clienteRepository.findByNumeroConta(numeroContaOrigem);
		if (clienteOrigem == null) {
			System.out.println("\u001B[31m" + "\nConta de origem não encontrada." + "\u001B[0m");
			return; // Sai do método se a conta de origem não for encontrada
		}

		// Solicita número da conta de destino
		System.out.println("\u001B[36m" + "\nDigite o número da conta de destino: " + "\u001B[0m");
		String numeroContaDestinoStr = scanner.nextLine();
		String numeroContaDestinoFormatado = Utils.removerNaoNumericos(numeroContaDestinoStr);
		int numeroContaDestino = Integer.parseInt(numeroContaDestinoFormatado);

		// Verifica se a conta de destino existe
		Cliente clienteDestino = clienteRepository.findByNumeroConta(numeroContaDestino);
		if (clienteDestino == null) {
			System.out.println("\u001B[31m" + "\nConta de destino não encontrada." + "\u001B[0m");
			return; // Sai do método se a conta de destino não for encontrada
		}

		// Solicita valor da transferência
		System.out.println("\u001B[36m" + "\nDigite o valor a ser transferido: " + "\u001B[0m");
		String valorTransferenciaStr = scanner.nextLine();
		String valorTransferenciaFormatado = Utils.removerNaoNumericos(valorTransferenciaStr);
		double valorTransferencia = Double.parseDouble(valorTransferenciaFormatado);

		// Verifica se o saldo da conta de origem é suficiente para a transferência
		if (clienteOrigem.getSaldo() < valorTransferencia) {
			System.out.println("\u001B[31m" + "\nSaldo insuficiente para realizar a transferência." + "\u001B[0m");
			return; // Sai do método se nao tiver o saldo 
		}

		// Confirma e efetuar a transferência
		System.out.println("\u001B[36m" + "\nConfirma a transferência de "
				+ Utils.formatarSaldoParaExibicao(valorTransferencia) + " da conta " + numeroContaOrigem
				+ " para a conta " + numeroContaDestino + "? (sim/não)" + "\u001B[0m");
		String confirmacao = scanner.nextLine();

		if (confirmacao.equalsIgnoreCase("sim")) {
			// Efetua a transferência
			clienteOrigem.setSaldo(clienteOrigem.getSaldo() - valorTransferencia);
			clienteDestino.setSaldo(clienteDestino.getSaldo() + valorTransferencia);
			clienteRepository.update(clienteOrigem);
			clienteRepository.update(clienteDestino);
			System.out.println("\u001B[32m" + "\nTransferência realizada com sucesso!" + "\u001B[0m");
		} else if (confirmacao.equalsIgnoreCase("não")) {
			System.out.println("\u001B[33m" + "\nOperação de transferência cancelada." + "\u001B[0m");
		} else {
			System.out.println("\u001B[31m" + "\nOpção inválida." + "\u001B[0m");
		}
	}

	public void consultarSaldo(Scanner scanner) {
		System.out.println("\u001B[36m" + "\nConsulta de Saldo" + "\u001B[0m");

		// Solicita número da conta
		System.out.println("\u001B[36m" + "\nDigite o número da conta: " + "\u001B[0m");
		String numeroContaStr = scanner.nextLine();
		String numeroContaFormatado = Utils.removerNaoNumericos(numeroContaStr);
		int numeroConta = Integer.parseInt(numeroContaFormatado);

		// Verifica se a conta existe
		Cliente cliente = clienteRepository.findByNumeroConta(numeroConta);
		if (cliente == null) {
			System.out.println("\u001B[31m" + "\nConta não encontrada." + "\u001B[0m");
			return; // Sai do método se a conta não for encontrada
		}

		// Exibe saldo do cliente
		System.out.println("\u001B[36m" + "\nSaldo disponível na conta: "
				+ Utils.formatarSaldoParaExibicao(cliente.getSaldo()) + "\u001B[0m");

		// Exibe as informações de taxas
		if (cliente.getTipoConta().equalsIgnoreCase("C")) {
			System.out.println("\n\u001B[36mInformações de Taxas:" + "\u001B[0m");
			if (cliente.getStatus().equalsIgnoreCase("COMUM")) {
				System.out.println("\nTaxa de Manutenção Mensal: R$ 12,00");
			} else if (cliente.getStatus().equalsIgnoreCase("SUPER")) {
				System.out.println("\nTaxa de Manutenção Mensal: R$ 8,00");
			} else if (cliente.getStatus().equalsIgnoreCase("PREMIUM")) {
				System.out.println("\nTaxa de Manutenção Mensal: Isenta");
			}
		} else if (cliente.getTipoConta().equalsIgnoreCase("P")) {
			System.out.println("\n\u001B[36mInformações de Taxas:" + "\u001B[0m");
			if (cliente.getStatus().equalsIgnoreCase("COMUM")) {
				System.out.println("\nTaxa de Rendimento Anual: 0,5% ao ano");
			} else if (cliente.getStatus().equalsIgnoreCase("SUPER")) {
				System.out.println("\nTaxa de Rendimento Anual: 0,7% ao ano");
			} else if (cliente.getStatus().equalsIgnoreCase("PREMIUM")) {
				System.out.println("\nTaxa de Rendimento Anual: 0,9% ao ano");
			}
			System.out.println("O rendimento é calculado mensalmente usando a fórmula do juro composto, baseando-se no saldo presente na conta no último dia do mês.");
		}
	}

	public void emitirCartaoDeCredito(Scanner scanner) {
	    System.out.println("\u001B[36m" + "\nEmitir Cartão de Crédito" + "\u001B[0m");
	    System.out.println("\u001B[36m" + "Digite o número da conta do cliente: " + "\u001B[0m");
	    int numeroConta = Integer.parseInt(scanner.nextLine());

	    Cliente cliente = clienteRepository.findByNumeroConta(numeroConta);

	    if (cliente != null) {
	        List<Cartao> cartoesDeCredito = cliente.getCartoesDeCredito();

	        if (cartoesDeCredito.size() >= 2) {
	            System.out.println("\u001B[31m" + "O cliente já possui o limite máximo de cartões de crédito (2)." + "\u001B[0m");
	            return;
	        }

	        Cartao novoCartao = new Cartao();
	        
	        // limite inicial com base na categoria do cliente
	        switch (cliente.getStatus()) {
	            case "COMUM":
	                novoCartao.setLimite(1000);
	                break;
	            case "SUPER":
	                novoCartao.setLimite(5000);
	                break;
	            case "PREMIUM":
	                novoCartao.setLimite(10000);
	                break;
	            default:
	                System.out.println("\u001B[31m" + "Categoria do cliente desconhecida." + "\u001B[0m");
	                return;
	        }
	        
	        // senha do cartão
	        while (true) {
	            System.out.println("\u001B[36m" + "Digite a senha do novo cartão (6 dígitos): " + "\u001B[0m");
	            String senhaStr = scanner.nextLine();
	            if (senhaStr.length() == 6 && senhaStr.matches("\\d+")) {
	                int senha = Integer.parseInt(senhaStr);
	                novoCartao.setSenha(senha);
	                break;
	            } else {
	                System.out.println("\u001B[31m" + "Senha inválida. A senha deve ter exatamente 6 dígitos." + "\u001B[0m");
	            }
	        }

	        cartoesDeCredito.add(novoCartao);
	        cliente.setCartoesDeCredito(cartoesDeCredito);
	        clienteRepository.update(cliente);

	        System.out.print("\u001B[32m" + "Cartão de crédito emitido com sucesso para o cliente " + "\u001B[0m");
	        System.out.println("\u001B[37m" + cliente.getNome() + "\u001B[0m");
	    } else {
	        System.out.println("\u001B[31m" + "Cliente com o número de conta especificado não encontrado." + "\u001B[0m");
	    }
	}

    public void pagarContaComCartao(Scanner scanner) {
        System.out.println("\u001B[36m" + "\nPagar Conta com Cartão de Crédito" + "\u001B[0m");
        System.out.println("\u001B[36m" + "\nDigite o número da conta do cliente: " + "\u001B[0m");
        int numeroConta = Integer.parseInt(scanner.nextLine());

        Cliente cliente = clienteRepository.findByNumeroConta(numeroConta);

        if (cliente != null) {
            List<Cartao> cartoesDeCredito = cliente.getCartoesDeCredito();

            if (cartoesDeCredito.isEmpty()) {
                System.out.println("\u001B[31m" + "O cliente não possui nenhum cartão de crédito para efetuar o pagamento." + "\u001B[0m");
                return;
            }

            // Mostra os cartões de crédito do usuario
            System.out.println("\u001B[36m" + "\nCartões de Crédito Disponíveis:" + "\u001B[0m");
            for (int i = 0; i < cartoesDeCredito.size(); i++) {
                System.out.println("\nCartão " +  (i + 1) + ". " + "Limite: " + Utils.formatarSaldoParaExibicao(cartoesDeCredito.get(i).getLimite()) +
                        " | Fatura: " + Utils.formatarSaldoParaExibicao(cartoesDeCredito.get(i).getFatura()));
            }

            System.out.println("\u001B[36m" + "\nEscolha o cartão de crédito para efetuar o pagamento (digite o número correspondente): " + "\u001B[0m");
            int escolhaCartao = Integer.parseInt(scanner.nextLine()) - 1;

            if (escolhaCartao < 0 || escolhaCartao >= cartoesDeCredito.size()) {
                System.out.println("\u001B[31m" + "Opção de cartão de crédito inválida." + "\u001B[0m");
                return;
            }

            Cartao cartaoSelecionado = cartoesDeCredito.get(escolhaCartao);

            System.out.println("\u001B[36m" + "Digite o valor da conta a pagar: " + "\u001B[0m");
            String valorContaStr = scanner.nextLine();
            double valorConta = Double.parseDouble(Utils.removerNaoNumericos(valorContaStr));

            // Verifica se o valor da conta ultrapassa 80% do limite
            double limite = cartaoSelecionado.getLimite();
            if (cartaoSelecionado.getFatura() + valorConta > 0.8 * limite) {
                // Calcula a taxa de utilização
                double taxaUtilizacao = (cartaoSelecionado.getFatura() + valorConta) * 0.05;

                System.out.println("\u001B[33m" + "O valor da conta ultrapassa 80% do limite de crédito.");
                System.out.println("Será aplicada uma taxa de utilização de 5% sobre o total gasto no mês, no valor de: " + Utils.formatarSaldoParaExibicao(taxaUtilizacao) + "\u001B[0m");

                // Atualiza a fatura e o limite do cartão com a taxa de utilização
                cartaoSelecionado.setFatura(cartaoSelecionado.getFatura() + valorConta + taxaUtilizacao);
                cartaoSelecionado.setLimite(limite - (valorConta + taxaUtilizacao));

                clienteRepository.update(cliente);
            } else {
                // Realiza o pagamento da conta sem taxa de utilização
                cartaoSelecionado.setFatura(cartaoSelecionado.getFatura() + valorConta);
                cartaoSelecionado.setLimite(limite - valorConta);

                clienteRepository.update(cliente);
            }

            System.out.println("\u001B[36m" + "\nPagamento efetuado com sucesso no cartão de crédito selecionado." + "\u001B[0m");
        } else {
            System.out.println("\u001B[31m" + "Cliente com o número de conta especificado não encontrado." + "\u001B[0m");
        }
    }
	
	public void alterarSenhaDoCartao(Scanner scanner) {
	    System.out.println("\u001B[36m" + "\nAlterar Senha do Cartão de Crédito" + "\u001B[0m");
	    System.out.println("\u001B[36m" + "Digite o número da conta do cliente: " + "\u001B[0m");
	    int numeroConta = Integer.parseInt(scanner.nextLine());

	    Cliente cliente = clienteRepository.findByNumeroConta(numeroConta);

	    if (cliente != null) {
	        List<Cartao> cartoesDeCredito = cliente.getCartoesDeCredito();

	        if (cartoesDeCredito.isEmpty()) {
	            System.out.println("\u001B[31m" + "O cliente não possui nenhum cartão de crédito." + "\u001B[0m");
	            return;
	        }

	        System.out.println("\u001B[36m" + "Cartões de Crédito Disponíveis:" + "\u001B[0m");
	        for (int i = 0; i < cartoesDeCredito.size(); i++) {
	            System.out.println("\nCartão " + (i + 1) + ". " + "Fatura: " + Utils.formatarSaldoParaExibicao(cartoesDeCredito.get(i).getFatura()));
	        }

	        System.out.println("\u001B[36m" + "\nEscolha o cartão de crédito para alterar a senha (digite o número correspondente): " + "\u001B[0m");
	        int escolhaCartao = Integer.parseInt(scanner.nextLine()) - 1;

	        if (escolhaCartao < 0 || escolhaCartao >= cartoesDeCredito.size()) {
	            System.out.println("\u001B[31m" + "Opção de cartão de crédito inválida." + "\u001B[0m");
	            return;
	        }

	        Cartao cartaoSelecionado = cartoesDeCredito.get(escolhaCartao);

	        System.out.println("\u001B[36m" + "Digite a nova senha (6 dígitos): " + "\u001B[0m");
	        String novaSenhaStr = scanner.nextLine();

	        if (novaSenhaStr.length() != 6 || !novaSenhaStr.matches("\\d{6}")) {
	            System.out.println("\u001B[31m" + "A senha deve conter exatamente 6 dígitos numéricos." + "\u001B[0m");
	            return;
	        }

	        int novaSenha = Integer.parseInt(novaSenhaStr);
	        cartaoSelecionado.setSenha(novaSenha);

	        clienteRepository.update(cliente);

	        System.out.println("\u001B[36m" + "\nSenha do cartão de crédito alterada com sucesso." + "\u001B[0m");
	    } else {
	        System.out.println("\u001B[31m" + "Cliente com o número de conta especificado não encontrado." + "\u001B[0m");
	    }
	}

    public void solicitarMaisLimite(Scanner scanner) {
        System.out.println("\u001B[36m" + "\nSolicitar Mais Limite do Cartão de Crédito" + "\u001B[0m");
        System.out.println("\u001B[36m" + "\nDigite o número da conta do cliente: " + "\u001B[0m");
        int numeroConta = Integer.parseInt(scanner.nextLine());

        Cliente cliente = clienteRepository.findByNumeroConta(numeroConta);

        if (cliente != null) {
            List<Cartao> cartoesDeCredito = cliente.getCartoesDeCredito();

            if (cartoesDeCredito.isEmpty()) {
                System.out.println("\u001B[31m" + "\nO cliente não possui nenhum cartão de crédito." + "\u001B[0m");
                return;
            }

            // Exibe os cartões de crédito usuario
            System.out.println("\u001B[36m" + "\nCartões de Crédito:" + "\u001B[0m");

            for (int i = 0; i < cartoesDeCredito.size(); i++) {
                Cartao cartao = cartoesDeCredito.get(i);
                System.out.println("\nCartão " + (i + 1) + ". Limite: " + Utils.formatarSaldoParaExibicao(cartao.getLimite()) +
                        " - Fatura: " + Utils.formatarSaldoParaExibicao(cartao.getFatura()));
            }

            System.out.println("\u001B[36m" + "\nEscolha o número do cartão de crédito que deseja aumentar o limite: " + "\u001B[0m");
            int escolhaCartao = Integer.parseInt(scanner.nextLine()) - 1;

            if (escolhaCartao < 0 || escolhaCartao >= cartoesDeCredito.size()) {
                System.out.println("\u001B[31m" + "\nOpção inválida." + "\u001B[0m");
                return;
            }

            // Realiza o sorteio
            boolean sorteio = Math.random() < 0.5; // 50% de chance de ser V ou F

            System.out.println("\u001B[36m" + "\nVerificando solicitação por favor aguarde... " + "\u001B[0m");
            try {
                Thread.sleep(5000); // Pausa por 5 segundos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restaura o estado de interrupção
                System.out.println("Houve uma interrupção na consulta do rating.");
            }

            if (sorteio) {
                // Aumenta o limite do cartão escolhido
                Cartao cartaoEscolhido = cartoesDeCredito.get(escolhaCartao);
                double novoLimite = cartaoEscolhido.getLimite() + getIncrementoLimite(cliente.getStatus());
                cartaoEscolhido.setLimite(novoLimite);

                clienteRepository.update(cliente);
                System.out.println("\u001B[36m" + "\nLimite do cartão de crédito aumentado com sucesso." + "\u001B[0m");
            } else {
                System.out.println("\u001B[31m" + "\nNão foi possível aumentar o limite do cartão de crédito neste momento." + "\u001B[0m");
            }
        } else {
            System.out.println("\u001B[31m" + "\nCliente com o número de conta especificado não encontrado." + "\u001B[0m");
        }
    }

    private double getIncrementoLimite(String status) {
        switch (status) {
            case "COMUM":
                return 1000.0; // Aumento de 1000 comum
            case "SUPER":
                return 5000.0; // Aumento de 5000 para super
            case "PREMIUM":
                return 10000.0; // Aumento de 10000 para premium
            default:
                return 0.0; // Retorno padrão, não deve ocorrer
        }
    }


	public void verFatura(Scanner scanner) {
		System.out.println("\u001B[36m" + "\nDigite o número da conta do cliente: " + "\u001B[0m");
		int numeroConta = Integer.parseInt(scanner.nextLine());

		Cliente cliente = clienteRepository.findByNumeroConta(numeroConta);

		if (cliente != null) {
			List<Cartao> cartoesDeCredito = cliente.getCartoesDeCredito();

			if (cartoesDeCredito.isEmpty()) {
				System.out.println("\u001B[31m" + "O cliente não possui nenhum cartão de crédito." + "\u001B[0m");
				return;
			}

			// Mostra os cartões de credito do usuario
			System.out.println("\u001B[36m" + "\nCartões de Crédito Disponíveis:" + "\u001B[0m");
			for (int i = 0; i < cartoesDeCredito.size(); i++) {
				System.out.println("\nCartão " + (i + 1) + ". " + "Limite: " + Utils.formatarSaldoParaExibicao(cartoesDeCredito.get(i).getLimite()) +
						" | Fatura: " + Utils.formatarSaldoParaExibicao(cartoesDeCredito.get(i).getFatura()));
			}
		}
	}

    public void contratarSeguro(Scanner scanner) {
        System.out.println("\u001B[36m" + "\nContratar Seguro para Cartão de Crédito" + "\u001B[0m");
        System.out.println("\u001B[36m" + "\nDigite o número da conta do cliente: " + "\u001B[0m");
        int numeroConta = Integer.parseInt(scanner.nextLine());

        Cliente cliente = clienteRepository.findByNumeroConta(numeroConta);

        if (cliente != null) {
            List<Cartao> cartoesDeCredito = cliente.getCartoesDeCredito();

            if (cartoesDeCredito.isEmpty()) {
                System.out.println("\u001B[31m" + "O cliente não possui nenhum cartão de crédito para contratar seguro." + "\u001B[0m");
                return;
            }

            // Mostra os cartões de credito do usuario
            System.out.println("\u001B[36m" + "\nCartões de Crédito Disponíveis:" + "\u001B[0m");
            for (int i = 0; i < cartoesDeCredito.size(); i++) {
                System.out.println("\nCartão " + (i + 1) + ". " + "Limite: " + Utils.formatarSaldoParaExibicao(cartoesDeCredito.get(i).getLimite()) +
                        " | Fatura: " + Utils.formatarSaldoParaExibicao(cartoesDeCredito.get(i).getFatura()));
            }

            System.out.println("\u001B[36m" + "\nEscolha o cartão de crédito para contratar seguro (digite o número correspondente): " + "\u001B[0m");
            int escolhaCartao = Integer.parseInt(scanner.nextLine()) - 1;

            if (escolhaCartao < 0 || escolhaCartao >= cartoesDeCredito.size()) {
                System.out.println("\u001B[31m" + "Opção de cartão de crédito inválida." + "\u001B[0m");
                return;
            }

            Cartao cartaoSelecionado = cartoesDeCredito.get(escolhaCartao);

            // Para cria um novo seguro no cartão
            SeguroCartao seguro = new SeguroCartao();
            System.out.println("\u001B[36m" + "\nEscolha o tipo de seguro a contratar: " + "\u001B[0m");
            System.out.println("\u001B[36m" + "1. Seguro Viagem" + "\u001B[0m");
            System.out.println("\u001B[36m" + "2. Seguro de Fraude" + "\u001B[0m");
            System.out.println("\u001B[36m" + "\nDigite a opção correspondente: " + "\u001B[0m");
            int escolhaSeguro = Integer.parseInt(scanner.nextLine());

            switch (escolhaSeguro) {
                case 1:
                    seguro.setTipo("Seguro Viagem");
                    if (!cliente.getStatus().equals("PREMIUM")) {
                        seguro.setValor(50.0); // Taxa para clientes Comum e Super
						cartaoSelecionado.setFatura(cartaoSelecionado.getFatura() + seguro.getValor());
						clienteRepository.save(cliente);
                        System.out.println("\u001B[36m" + "Seguro Viagem contratado com sucesso. Taxa de R$ 50,00 será adicionada à fatura mensal." + "\u001B[0m");

						try {
							ApoliceSeguro apoliceSeguro = new ApoliceSeguro(escolhaCartao, TipoApoliceEnum.VIAGEM, 10000);
						} catch(IOException	e) {
							System.out.println("\u001B[31m" + "Ocorreu um erro ao gerar o apólice." + "\u001B[0m");
						}
						System.out.println("\u001B[36m" + "Apólice gerado." + "\u001B[0m");

					} else {
                        System.out.println("\u001B[36m" + "O cliente já possui Seguro Viagem gratuito." + "\u001B[0m");
                    }
                    break;
                case 2:
                    seguro.setTipo("Seguro de Fraude");
                    seguro.setValor(0.0); // Seguro de fraude free
                    System.out.println("\u001B[36m" + "Seguro de Fraude contratado com sucesso." + "\u001B[0m");
					try {
						ApoliceSeguro apoliceSeguro = new ApoliceSeguro(escolhaCartao, TipoApoliceEnum.FRAUDE, 5000);
					} catch(IOException	e) {
						System.out.println("\u001B[31m" + "Ocorreu um erro ao gerar o apólice." + "\u001B[0m");
					}
					System.out.println("\u001B[36m" + "Apólice gerado." + "\u001B[0m");
                    break;
                default:
                    System.out.println("\u001B[31m" + "Opção de seguro inválida." + "\u001B[0m");
                    return;
            }

            // Adiciona o seguro/cartão
            cartaoSelecionado.getSeguros().add(seguro);

            clienteRepository.update(cliente);

        } else {
            System.out.println("\u001B[31m" + "Cliente com o número de conta especificado não encontrado." + "\u001B[0m");
        }
    }
}
