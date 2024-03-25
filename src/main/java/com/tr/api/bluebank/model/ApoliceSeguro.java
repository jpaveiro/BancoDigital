package com.tr.api.bluebank.model;

import com.tr.api.bluebank.enums.TipoApoliceEnum;
import lombok.Data;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


@Data
public class ApoliceSeguro {
    private Random random = new Random();
    private long numeroApolice = random.nextLong(999999999);

    private FileWriter writer;
    public ApoliceSeguro(int cartaoEscolhido, TipoApoliceEnum tipoApoliceEnum, double valorAssegurado) throws IOException {

        if (tipoApoliceEnum == tipoApoliceEnum.VIAGEM) {
            writer = new FileWriter("apoliceViagem.txt");
        } else if (tipoApoliceEnum == tipoApoliceEnum.FRAUDE) {
            writer = new FileWriter("apoliceFraude.txt");
        }

        DecimalFormat formatoDecimal = new DecimalFormat("#.00");
        String valorFormatado = formatoDecimal.format(valorAssegurado);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        String dataEmissao = sdf.format(new Date());
        writer.write("Emitido por " + " BlueBank | " + dataEmissao + "\n");
        writer.write("Cartão nº" + (cartaoEscolhido + 1) + " | Número do apólice: " + numeroApolice + "\n");
        writer.write("Valor assegurado: " + valorFormatado + "\n");

        if (tipoApoliceEnum == tipoApoliceEnum.VIAGEM) {
            writer.write("Tipo: " + "Viagem\n");
            writer.write("Descrição: " + TipoApoliceEnum.VIAGEM.getDescricao());
        } else if (tipoApoliceEnum == tipoApoliceEnum.FRAUDE) {
            writer.write("Tipo: " + "Fraude\n");
            writer.write("Descrição: " + tipoApoliceEnum.FRAUDE.getDescricao());
        }

        writer.close();

    }
}
