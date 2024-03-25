package com.tr.api.bluebank;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.tr.api.bluebank.repository.ClienteRepository;
import com.tr.api.bluebank.service.ClienteService;
import com.tr.api.bluebank.util.Utils;

public class BlueBankApplication {

	private static ClienteRepository clienteRepository = new ClienteRepository();
	private static Utils utils = new Utils(clienteRepository);

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		ClienteService clienteService = new ClienteService(clienteRepository, utils);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		boolean continuar = true;

		System.out.println("\u001B[34m" + "\nSEJA BEM VINDO AO BLUE BANK" + "\u001B[0m");

/*
		MENU PRINCIPAL
*/
			while (continuar) {
				try {
					boolean opcaoValida = false;
					while (!opcaoValida) {
						System.out.println("\u001B[33m" + "\nMenu Principal" + "\u001B[0m");
						System.out.println("\u001B[36m" + "\n1 - Cadastrar novo cliente" + "\u001B[0m");
						System.out.println("\u001B[36m" + "2 - Operações Bancárias" + "\u001B[0m");
						System.out.println("\u001B[36m" + "3 - Cartão de Crédito" + "\u001B[0m");
						System.out.println("\u001B[36m" + "4 - Sair" + "\u001B[0m");
						System.out.println("\u001B[36m" + "\nEscolha uma opção: " + "\u001B[0m");

						String escolha = scanner.nextLine();

						switch (escolha) {
							case "1":
								clienteService.cadastrarCliente(scanner, clienteService, formatter);
								opcaoValida = true;
								break;
							case "2":
								operacoesBancarias(scanner, clienteService);
								break;
							case "3":
								cartaoDeCredito(scanner, clienteService);
								break;
							case "4":
								continuar = false;
								opcaoValida = true;
								scanner.close();
								System.out.println("\u001B[34m" + "\nOBRIGADO POR UTILIZAR O BLUE BANK" + "\u001B[0m");
								return;
							default:
								System.out.println("\u001B[31m" + "Opção inválida." + "\u001B[0m");
								break;
						}
					}

					boolean continuarCadastro = true;
					while (continuarCadastro) {
						System.out.println("\u001B[36m"
								+ "\nDigite 'novo' para cadastrar outro cliente, 'voltar' para retornar ao menu principal ou 'sair' para encerrar:"
								+ "\u001B[0m");
						String opcao = scanner.nextLine();
						switch (opcao.toLowerCase()) {
							case "novo":
								clienteService.cadastrarCliente(scanner, clienteService, formatter);
								break;
							case "voltar":
								continuarCadastro = false;
								break;
							case "sair":
								continuar = false;
								continuarCadastro = false;
								break;
							default:
								System.out.println("\u001B[31m"
										+ "\nOpção inválida. Por favor, digite 'novo' para cadastrar outro cliente, 'voltar' para retornar ao menu principal ou 'sair' para encerrar."
										+ "\u001B[0m");
								break;
						}
					}
				} catch (InputMismatchException error) {
					System.out.println("\u001B[31m" + "Opção inválida." + "\u001B[0m");
					System.out.println("\u001B[31m" + "Digite algo para continuar." + "\u001B[0m");
					scanner.nextLine();
				} catch (NumberFormatException error) {
					System.out.println("\u001B[31m" + "O que você forneceu é inválido." + "\u001B[0m");
					System.out.println("\u001B[31m" + "Digite algo para continuar." + "\u001B[0m");
					scanner.nextLine();
				} catch (Exception error) {
					System.out.println("\u001B[31m" + "Ocorreu um erro durante a execução: " + error.getMessage() + "\u001B[0m");
					System.out.println("\u001B[31m" + "Digite algo para continuar." + "\u001B[0m");
					scanner.nextLine();
				}
			}

		scanner.close();
		System.out.println("\u001B[34m" + "\nOBRIGADO POR UTILIZAR O BLUE BANK" + "\u001B[0m");
	}

/*
		MENU OPERAÇÕES BANCÁRIAS
*/

	private static void operacoesBancarias(Scanner scanner, ClienteService clienteService) {
		boolean continuarFuncionalidade2 = true;
		while (continuarFuncionalidade2) {
			boolean opcaoValida = false;
			while (!opcaoValida) {
				System.out.println("\u001B[33m" + "\nMenu Operações Bancárias" + "\u001B[0m");
				System.out.println("\u001B[36m" + "\n1 - Depósito" + "\u001B[0m");
				System.out.println("\u001B[36m" + "2 - Saque" + "\u001B[0m");
				System.out.println("\u001B[36m" + "3 - Transferência via PIX" + "\u001B[0m");
				System.out.println("\u001B[36m" + "4 - Consultar saldo" + "\u001B[0m");
				System.out.println("\u001B[36m" + "5 - Voltar ao menu principal" + "\u001B[0m");
				System.out.println("\u001B[36m" + "\nEscolha uma opção: " + "\u001B[0m");

				String escolhaFuncionalidade2 = scanner.nextLine();

				switch (escolhaFuncionalidade2) {
				case "1":
					clienteService.deposito(scanner);
					opcaoValida = true;
					break;
				case "2":
					clienteService.saque(scanner);
					opcaoValida = true;
					break;
				case "3":
					clienteService.realizarPix(scanner);
					opcaoValida = true;
					break;
				case "4":
					clienteService.consultarSaldo(scanner);
					opcaoValida = true;
					break;
				case "5":
					continuarFuncionalidade2 = false;
					opcaoValida = true;
					break;
				default:
					System.out.println("\u001B[31m" + "Opção inválida." + "\u001B[0m");
					break;
				}
			}
		}
	}
	
	private static void cartaoDeCredito(Scanner scanner, ClienteService clienteService) {
		boolean continuarFuncionalidade2 = true;
		while (continuarFuncionalidade2) {
			boolean opcaoValida = false;
			while (!opcaoValida) {
				System.out.println("\u001B[33m" + "\nMenu Cartão de Crédito" + "\u001B[0m");
				System.out.println("\u001B[36m" + "\n1 - Emitir um novo cartão" + "\u001B[0m");
				System.out.println("\u001B[36m" + "2 - Fazer compra com Cartão de Crédito" + "\u001B[0m");
				System.out.println("\u001B[36m" + "3 - Alterar Senha" + "\u001B[0m");
				System.out.println("\u001B[36m" + "4 - Solicitar Limite" + "\u001B[0m");
				System.out.println("\u001B[36m" + "5 - Contratar Seguro" + "\u001B[0m");
				System.out.println("\u001B[36m" + "6 - Ver fatura" + "\u001B[0m");
				System.out.println("\u001B[36m" + "7 - Voltar ao menu principal" + "\u001B[0m");
				System.out.println("\u001B[36m" + "\nEscolha uma opção: " + "\u001B[0m");

				String escolhaFuncionalidade2 = scanner.nextLine();

				switch (escolhaFuncionalidade2) {
				case "1":
					clienteService.emitirCartaoDeCredito(scanner);
					opcaoValida = true;
					break;
				case "2":
					clienteService.pagarContaComCartao(scanner);
					opcaoValida = true;
					break;
				case "3":
					clienteService.alterarSenhaDoCartao(scanner);
					opcaoValida = true;
					break;
				case "4":
					clienteService.solicitarMaisLimite(scanner);
					opcaoValida = true;
					break;
				case "5":
					clienteService.contratarSeguro(scanner);
					opcaoValida = true;
					break;
				case "6":
					clienteService.verFatura(scanner);
					opcaoValida = true;
					break;
				case "7":
					continuarFuncionalidade2 = false;
					opcaoValida = true;
					break;
				default:
					System.out.println("\u001B[31m" + "Opção inválida." + "\u001B[0m");
					break;
				}
			}
		}
	}
}