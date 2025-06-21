package com.totalelectro.service.impl;

import com.totalelectro.service.ShippingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

@Service
public class ShippingServiceImpl implements ShippingService {
    
    private static final Logger logger = LoggerFactory.getLogger(ShippingServiceImpl.class);
    
    @Value("${app.shipping.base-rate:5.0}")
    private double baseRate; // Taxa base do frete
    
    @Value("${app.shipping.rate-per-km:0.5}")
    private double ratePerKm; // Taxa por quilômetro
    
    @Value("${app.shipping.free-shipping-threshold:100.0}")
    private double freeShippingThreshold; // Valor mínimo para frete grátis
    
    @Value("${app.shipping.max-distance:50.0}")
    private double maxDistance; // Distância máxima para entrega
    
    @Value("${app.shipping.store-cep:97574-230}")
    private String storeCep; // CEP da loja
    
    @Value("${app.shipping.store-weight:1.0}")
    private double packageWeight; // Peso do pacote em kg
    
    @Value("${app.shipping.store-length:20.0}")
    private double packageLength; // Comprimento em cm
    
    @Value("${app.shipping.store-width:15.0}")
    private double packageWidth; // Largura em cm
    
    @Value("${app.shipping.store-height:10.0}")
    private double packageHeight; // Altura em cm
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public ShippingServiceImpl() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public double calculateShipping(String destinationCep) {
        try {
            String originCep = this.storeCep;
            String cleanDestinationCep = extractCep(destinationCep);

            if (cleanDestinationCep == null) {
                throw new RuntimeException("CEP de destino inválido. O formato esperado é 00000-000.");
            }
            
            return calculateShippingByCep(originCep, cleanDestinationCep);
        } catch (RuntimeException e) {
            logger.error("Erro final ao calcular frete para o CEP {}: {}", destinationCep, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erro inesperado ao calcular frete para o CEP {}: {}", destinationCep, e.getMessage());
            throw new RuntimeException("Ocorreu um erro inesperado ao calcular o frete.", e);
        }
    }
    
    @Override
    public double calculateShippingByDistance(double distanceInKm) {
        // Verificar se a distância está dentro do limite
        if (distanceInKm > maxDistance) {
            throw new RuntimeException("Distância muito longa para entrega. Máximo permitido: " + maxDistance + "km");
        }
        
        // Calcular frete baseado na distância
        double shippingCost = baseRate + (distanceInKm * ratePerKm);
        
        // Arredondar para 2 casas decimais
        return Math.round(shippingCost * 100.0) / 100.0;
    }
    
    @Override
    public double getDistance(String destinationCep) {
        try {
            String originCep = this.storeCep;
            String cleanDestinationCep = extractCep(destinationCep);

            if (cleanDestinationCep == null) {
                throw new RuntimeException("CEP de destino inválido para cálculo de distância.");
            }

            return getDistanceFromCorreios(originCep, cleanDestinationCep);
        } catch (Exception e) {
            logger.error("Não foi possível obter a distância para o CEP {}", destinationCep, e);
            throw new RuntimeException("Não foi possível obter a distância a partir do CEP informado.", e);
        }
    }
    
    private double calculateShippingByCep(String originCep, String destinationCep) {
        try {
            return getShippingFromCorreios(originCep, destinationCep);
        } catch (Exception e) {
            logger.error("Falha ao consultar a API dos Correios para cálculo de frete.", e);
            throw new RuntimeException("Não foi possível calcular o frete. O serviço dos Correios pode estar indisponível. Tente novamente mais tarde.", e);
        }
    }
    
    private double getShippingFromCorreios(String originCep, String destinationCep) {
        // API dos Correios - Calcular Preço e Prazo
        String url = "http://ws.correios.com.br/calculador/CalcPrecoPrazo.aspx";
        
        // Parâmetros da API dos Correios
        String params = String.format(
            "?nCdEmpresa=&sDsSenha=&nCdServico=04510&sCepOrigem=%s&sCepDestino=%s&nVlPeso=%.2f&nCdFormato=1&nVlComprimento=%.1f&nVlAltura=%.1f&nVlLargura=%.1f&nVlDiametro=0&sCdMaoPropria=n&nVlValorDeclarado=0&sCdAvisoRecebimento=n&StrRetorno=xml",
            originCep.replace("-", ""),
            destinationCep.replace("-", ""),
            packageWeight,
            packageLength,
            packageHeight,
            packageWidth
        );
        
        String fullUrl = url + params;
        
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(fullUrl, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseCorreiosResponse(response.getBody());
            }
        } catch (Exception e) {
            logger.error("Erro na requisição para API dos Correios: {}", e.getMessage());
        }
        
        throw new RuntimeException("Não foi possível calcular o frete pelos Correios");
    }
    
    private double parseCorreiosResponse(String xmlResponse) {
        try {
            // Parse básico do XML dos Correios
            if (xmlResponse.contains("<Erro>")) {
                String error = extractXmlValue(xmlResponse, "MsgErro");
                throw new RuntimeException("Erro dos Correios: " + error);
            }
            
            String valor = extractXmlValue(xmlResponse, "Valor");
            if (valor != null && !valor.isEmpty()) {
                // Converter de "0,00" para 0.00
                return Double.parseDouble(valor.replace(",", "."));
            }
            
            throw new RuntimeException("Valor do frete não encontrado na resposta");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar resposta dos Correios: " + e.getMessage());
        }
    }
    
    private String extractXmlValue(String xml, String tag) {
        String startTag = "<" + tag + ">";
        String endTag = "</" + tag + ">";
        
        int start = xml.indexOf(startTag);
        if (start == -1) return null;
        
        start += startTag.length();
        int end = xml.indexOf(endTag, start);
        if (end == -1) return null;
        
        return xml.substring(start, end).trim();
    }
    
    private double getDistanceFromCorreios(String originCep, String destinationCep) {
        // Usar API de CEP para obter coordenadas e calcular distância
        try {
            Map<String, Object> originData = getCepData(originCep);
            Map<String, Object> destinationData = getCepData(destinationCep);
            
            if (originData != null && destinationData != null) {
                return calculateDistanceFromCoordinates(
                    (Double) originData.get("latitude"),
                    (Double) originData.get("longitude"),
                    (Double) destinationData.get("latitude"),
                    (Double) destinationData.get("longitude")
                );
            }
        } catch (Exception e) {
            logger.warn("Erro ao obter dados do CEP: {}", e.getMessage());
        }
        
        return calculateSimulatedDistanceByCep(originCep, destinationCep);
    }
    
    private Map<String, Object> getCepData(String cep) {
        try {
            String url = "https://viacep.com.br/ws/" + cep.replace("-", "") + "/json/";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode data = objectMapper.readTree(response.getBody());
                
                if (!data.has("erro")) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("cep", data.get("cep").asText());
                    result.put("logradouro", data.get("logradouro").asText());
                    result.put("bairro", data.get("bairro").asText());
                    result.put("localidade", data.get("localidade").asText());
                    result.put("uf", data.get("uf").asText());
                    
                    // Coordenadas aproximadas baseadas na cidade (simulado)
                    result.put("latitude", getLatitudeByCity(data.get("localidade").asText()));
                    result.put("longitude", getLongitudeByCity(data.get("localidade").asText()));
                    
                    return result;
                }
            }
        } catch (Exception e) {
            logger.warn("Erro ao buscar dados do CEP {}: {}", cep, e.getMessage());
        }
        
        return null;
    }
    
    private double getLatitudeByCity(String city) {
        // Coordenadas simuladas para algumas cidades principais
        Map<String, Double> cityCoordinates = new HashMap<>();
        cityCoordinates.put("São Paulo", -23.5505);
        cityCoordinates.put("Rio de Janeiro", -22.9068);
        cityCoordinates.put("Belo Horizonte", -19.9167);
        cityCoordinates.put("Brasília", -15.7942);
        cityCoordinates.put("Salvador", -12.9714);
        cityCoordinates.put("Fortaleza", -3.7319);
        cityCoordinates.put("Recife", -8.0476);
        cityCoordinates.put("Porto Alegre", -30.0346);
        cityCoordinates.put("Curitiba", -25.4289);
        cityCoordinates.put("Goiânia", -16.6864);
        cityCoordinates.put("Santa Maria", -29.6868);
        cityCoordinates.put("Santana do Livramento", -30.8925);
        
        return cityCoordinates.getOrDefault(city, -30.8925); // Default para Santana do Livramento
    }
    
    private double getLongitudeByCity(String city) {
        // Coordenadas simuladas para algumas cidades principais
        Map<String, Double> cityCoordinates = new HashMap<>();
        cityCoordinates.put("São Paulo", -46.6333);
        cityCoordinates.put("Rio de Janeiro", -43.1729);
        cityCoordinates.put("Belo Horizonte", -43.9345);
        cityCoordinates.put("Brasília", -47.8822);
        cityCoordinates.put("Salvador", -38.5014);
        cityCoordinates.put("Fortaleza", -38.5267);
        cityCoordinates.put("Recife", -34.8770);
        cityCoordinates.put("Porto Alegre", -51.2177);
        cityCoordinates.put("Curitiba", -49.2671);
        cityCoordinates.put("Goiânia", -49.2653);
        cityCoordinates.put("Santa Maria", -53.8149);
        cityCoordinates.put("Santana do Livramento", -55.5328);
        
        return cityCoordinates.getOrDefault(city, -55.5328); // Default para Santana do Livramento
    }
    
    private double calculateDistanceFromCoordinates(double lat1, double lon1, double lat2, double lon2) {
        // Fórmula de Haversine para calcular distância entre coordenadas
        final int R = 6371; // Raio da Terra em km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    private double getDistanceFromCep(String originCep, String destinationCep) {
        return getDistanceFromCorreios(originCep, destinationCep);
    }
    
    private double calculateSimulatedDistanceByCep(String originCep, String destinationCep) {
        // Cálculo simulado baseado em diferenças de CEP
        String originUf = extractUfFromCep(originCep);
        String destinationUf = extractUfFromCep(destinationCep);
        
        if (originUf.equals(destinationUf)) {
            return Math.random() * 10 + 5; // 5-15 km no mesmo estado
        } else {
            return Math.random() * 100 + 50; // 50-150 km entre estados
        }
    }
    
    private String extractUfFromCep(String cep) {
        // Extrair UF baseado no CEP (implementação básica)
        String cepClean = cep.replace("-", "");
        if (cepClean.length() >= 2) {
            // Mapeamento básico de CEP para UF
            String prefix = cepClean.substring(0, 2);
            Map<String, String> cepToUf = new HashMap<>();
            cepToUf.put("01", "DF"); cepToUf.put("02", "DF"); cepToUf.put("03", "DF");
            cepToUf.put("04", "DF"); cepToUf.put("05", "DF"); cepToUf.put("06", "DF");
            cepToUf.put("07", "DF"); cepToUf.put("08", "DF"); cepToUf.put("09", "DF");
            cepToUf.put("10", "SP"); cepToUf.put("11", "SP"); cepToUf.put("12", "SP");
            cepToUf.put("13", "SP"); cepToUf.put("14", "SP"); cepToUf.put("15", "SP");
            cepToUf.put("16", "SP"); cepToUf.put("17", "SP"); cepToUf.put("18", "SP");
            cepToUf.put("19", "SP"); cepToUf.put("20", "RJ"); cepToUf.put("21", "RJ");
            cepToUf.put("22", "RJ"); cepToUf.put("23", "RJ"); cepToUf.put("24", "RJ");
            cepToUf.put("25", "RJ"); cepToUf.put("26", "RJ"); cepToUf.put("27", "RJ");
            cepToUf.put("28", "RJ"); cepToUf.put("29", "RJ"); cepToUf.put("30", "MG");
            cepToUf.put("31", "MG"); cepToUf.put("32", "MG"); cepToUf.put("33", "MG");
            cepToUf.put("34", "MG"); cepToUf.put("35", "MG"); cepToUf.put("36", "MG");
            cepToUf.put("37", "MG"); cepToUf.put("38", "MG"); cepToUf.put("39", "MG");
            cepToUf.put("40", "BA"); cepToUf.put("41", "BA"); cepToUf.put("42", "BA");
            cepToUf.put("43", "BA"); cepToUf.put("44", "BA"); cepToUf.put("45", "BA");
            cepToUf.put("46", "BA"); cepToUf.put("47", "BA"); cepToUf.put("48", "BA");
            cepToUf.put("49", "BA"); cepToUf.put("50", "PE"); cepToUf.put("51", "PE");
            cepToUf.put("52", "PE"); cepToUf.put("53", "PE"); cepToUf.put("54", "PE");
            cepToUf.put("55", "PE"); cepToUf.put("56", "PE"); cepToUf.put("57", "PE");
            cepToUf.put("58", "PE"); cepToUf.put("59", "PE"); cepToUf.put("60", "CE");
            cepToUf.put("61", "CE"); cepToUf.put("62", "CE"); cepToUf.put("63", "CE");
            cepToUf.put("64", "CE"); cepToUf.put("65", "CE"); cepToUf.put("66", "CE");
            cepToUf.put("67", "CE"); cepToUf.put("68", "CE"); cepToUf.put("69", "CE");
            cepToUf.put("70", "GO"); cepToUf.put("71", "GO"); cepToUf.put("72", "GO");
            cepToUf.put("73", "GO"); cepToUf.put("74", "GO"); cepToUf.put("75", "GO");
            cepToUf.put("76", "GO"); cepToUf.put("77", "GO"); cepToUf.put("78", "GO");
            cepToUf.put("79", "GO"); cepToUf.put("80", "PR"); cepToUf.put("81", "PR");
            cepToUf.put("82", "PR"); cepToUf.put("83", "PR"); cepToUf.put("84", "PR");
            cepToUf.put("85", "PR"); cepToUf.put("86", "PR"); cepToUf.put("87", "PR");
            cepToUf.put("88", "PR"); cepToUf.put("89", "PR"); cepToUf.put("90", "RS");
            cepToUf.put("91", "RS"); cepToUf.put("92", "RS"); cepToUf.put("93", "RS");
            cepToUf.put("94", "RS"); cepToUf.put("95", "RS"); cepToUf.put("96", "RS");
            cepToUf.put("97", "RS"); cepToUf.put("98", "RS"); cepToUf.put("99", "RS");
            
            // Mapeamento específico para Santa Maria, RS
            if (cepClean.equals("97574230")) {
                return "RS";
            }
            
            return cepToUf.getOrDefault(prefix, "RS");
        }
        return "RS";
    }
    
    private String extractCep(String address) {
        // Extrair CEP do endereço usando regex
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d{5}-?\\d{3}");
        java.util.regex.Matcher matcher = pattern.matcher(address);
        
        if (matcher.find()) {
            return matcher.group();
        }
        
        return null;
    }
    
    // Métodos auxiliares para configuração
    public void setBaseRate(double baseRate) {
        this.baseRate = baseRate;
    }
    
    public void setRatePerKm(double ratePerKm) {
        this.ratePerKm = ratePerKm;
    }
    
    public void setFreeShippingThreshold(double freeShippingThreshold) {
        this.freeShippingThreshold = freeShippingThreshold;
    }
    
    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }
    
    public double getBaseRate() {
        return baseRate;
    }
    
    public double getRatePerKm() {
        return ratePerKm;
    }
    
    public double getFreeShippingThreshold() {
        return freeShippingThreshold;
    }
    
    public double getMaxDistance() {
        return maxDistance;
    }

    @Override
    public String getStoreCep() {
        return this.storeCep;
    }
} 