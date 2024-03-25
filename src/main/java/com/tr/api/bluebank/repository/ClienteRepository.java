package com.tr.api.bluebank.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tr.api.bluebank.model.Cliente;

public class ClienteRepository {
    
    private static final String JSON_FILE_PATH = "src/main/resources/clientes.json";
    private final ObjectMapper objectMapper;

    public ClienteRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public List<Cliente> findAll() {
        File file = new File(JSON_FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(file, new TypeReference<List<Cliente>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Cliente save(Cliente cliente) {
        List<Cliente> clientes = findAll();
        clientes.add(cliente);
        try {
            // Aqui vc faz a gravar da lista 
            objectMapper.writeValue(new File(JSON_FILE_PATH), clientes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cliente;
    }
    
    public boolean cpfExists(String cpf) {
        List<Cliente> clientes = findAll();
        for (Cliente cliente : clientes) {
            if (cliente.getCpf().equals(cpf)) {
                return true;
            }
        }
        return false;
    }
    
    public Cliente findByNumeroConta(int numeroConta) {
        List<Cliente> clientes = findAll();
        for (Cliente cliente : clientes) {
            if (cliente.getNumeroConta() == numeroConta) {
                return cliente;
            }
        }
        return null;
    }
    
    public boolean update(Cliente cliente) {
        List<Cliente> clientes = findAll();
        if (clientes == null) {
            return false;
        }
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getId().equals(cliente.getId())) {
                clientes.set(i, cliente);
                try {
                    objectMapper.writeValue(new File(JSON_FILE_PATH), clientes);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }
}

