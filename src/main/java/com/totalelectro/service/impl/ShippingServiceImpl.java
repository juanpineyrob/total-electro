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

            return getDistanceFromCoordinates(originCep, cleanDestinationCep);
        } catch (Exception e) {
            logger.error("Não foi possível obter a distância para o CEP {}", destinationCep, e);
            throw new RuntimeException("Não foi possível obter a distância a partir do CEP informado.", e);
        }
    }
    
    private double calculateShippingByCep(String originCep, String destinationCep) {
        try {
            // Primeiro, tentar calcular baseado em coordenadas reais
            double distance = getDistanceFromCoordinates(originCep, destinationCep);
            return calculateShippingByDistance(distance);
        } catch (Exception e) {
            logger.warn("Falha ao calcular frete por coordenadas, usando cálculo regional: {}", e.getMessage());
            // Fallback para cálculo regional
            return calculateRegionalShipping(originCep, destinationCep);
        }
    }
    
    private double getDistanceFromCoordinates(String originCep, String destinationCep) {
        try {
            Map<String, Object> originData = getCepData(originCep);
            Map<String, Object> destinationData = getCepData(destinationCep);
            
            if (originData != null && destinationData != null) {
                Double originLat = (Double) originData.get("latitude");
                Double originLon = (Double) originData.get("longitude");
                Double destLat = (Double) destinationData.get("latitude");
                Double destLon = (Double) destinationData.get("longitude");
                
                if (originLat != null && originLon != null && destLat != null && destLon != null) {
                    return calculateDistanceFromCoordinates(originLat, originLon, destLat, destLon);
                }
            }
        } catch (Exception e) {
            logger.warn("Erro ao obter coordenadas para cálculo de distância: {}", e.getMessage());
        }
        
        // Fallback para cálculo simulado baseado em região
        return calculateSimulatedDistanceByCep(originCep, destinationCep);
    }
    
    private double calculateRegionalShipping(String originCep, String destinationCep) {
        String originRegion = getRegionFromCep(originCep);
        String destinationRegion = getRegionFromCep(destinationCep);
        
        // Tabela de preços por região (otimizada para RS)
        Map<String, Double> regionalPrices = new HashMap<>();
        
        // Rio Grande do Sul - preços mais detalhados
        regionalPrices.put("RS", 12.0); // Preço base para RS
        
        // Outros estados
        regionalPrices.put("SC", 22.0); // Santa Catarina
        regionalPrices.put("PR", 28.0); // Paraná
        regionalPrices.put("SP", 42.0); // São Paulo
        regionalPrices.put("RJ", 48.0); // Rio de Janeiro
        regionalPrices.put("MG", 38.0); // Minas Gerais
        regionalPrices.put("ES", 42.0); // Espírito Santo
        regionalPrices.put("BA", 52.0); // Bahia
        regionalPrices.put("SE", 58.0); // Sergipe
        regionalPrices.put("AL", 58.0); // Alagoas
        regionalPrices.put("PE", 62.0); // Pernambuco
        regionalPrices.put("PB", 62.0); // Paraíba
        regionalPrices.put("RN", 68.0); // Rio Grande do Norte
        regionalPrices.put("CE", 72.0); // Ceará
        regionalPrices.put("PI", 68.0); // Piauí
        regionalPrices.put("MA", 72.0); // Maranhão
        regionalPrices.put("PA", 78.0); // Pará
        regionalPrices.put("AP", 82.0); // Amapá
        regionalPrices.put("AM", 88.0); // Amazonas
        regionalPrices.put("RR", 92.0); // Roraima
        regionalPrices.put("AC", 88.0); // Acre
        regionalPrices.put("RO", 82.0); // Rondônia
        regionalPrices.put("TO", 72.0); // Tocantins
        regionalPrices.put("GO", 48.0); // Goiás
        regionalPrices.put("MT", 68.0); // Mato Grosso
        regionalPrices.put("MS", 52.0); // Mato Grosso do Sul
        regionalPrices.put("DF", 48.0); // Distrito Federal
        
        // Se mesma região (RS), aplicar desconto baseado na proximidade
        if (originRegion.equals(destinationRegion)) {
            double basePrice = regionalPrices.getOrDefault(destinationRegion, 15.0);
            
            // Verificar se é uma cidade próxima a Santana do Livramento
            if (isNearbyCity(destinationCep)) {
                return basePrice * 0.6; // 40% de desconto para cidades próximas
            } else {
                return basePrice * 0.8; // 20% de desconto para outras cidades do RS
            }
        }
        
        return regionalPrices.getOrDefault(destinationRegion, 25.0);
    }
    
    private boolean isNearbyCity(String destinationCep) {
        // Cidades próximas a Santana do Livramento (RS)
        String[] nearbyCeps = {
            "97574", // Santana do Livramento
            "97570", // Santana do Livramento
            "97575", // Santana do Livramento
            "97576", // Santana do Livramento
            "97577", // Santana do Livramento
            "97578", // Santana do Livramento
            "97579", // Santana do Livramento
            "97580", // Santana do Livramento
            "97581", // Santana do Livramento
            "97582", // Santana do Livramento
            "97583", // Santana do Livramento
            "97584", // Santana do Livramento
            "97585", // Santana do Livramento
            "97586", // Santana do Livramento
            "97587", // Santana do Livramento
            "97588", // Santana do Livramento
            "97589", // Santana do Livramento
            "97590", // Santana do Livramento
            "97591", // Santana do Livramento
            "97592", // Santana do Livramento
            "97593", // Santana do Livramento
            "97594", // Santana do Livramento
            "97595", // Santana do Livramento
            "97596", // Santana do Livramento
            "97597", // Santana do Livramento
            "97598", // Santana do Livramento
            "97599"  // Santana do Livramento
        };
        
        String cepPrefix = destinationCep.replace("-", "").substring(0, 5);
        for (String nearbyCep : nearbyCeps) {
            if (cepPrefix.equals(nearbyCep)) {
                return true;
            }
        }
        
        return false;
    }
    
    private String getRegionFromCep(String cep) {
        String cepClean = cep.replace("-", "");
        if (cepClean.length() >= 2) {
            String prefix = cepClean.substring(0, 2);
            Map<String, String> cepToUf = new HashMap<>();
            
            // Mapeamento completo de CEP para UF
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
            
            return cepToUf.getOrDefault(prefix, "RS");
        }
        return "RS";
    }
    
    private Map<String, Object> getCepData(String cep) {
        try {
            // Mapeamento específico para o CEP da loja
            if ("97574-230".equals(cep)) {
                Map<String, Object> result = new HashMap<>();
                result.put("cep", "97574-230");
                result.put("logradouro", "Rua da Loja");
                result.put("bairro", "Centro");
                result.put("localidade", "Santana do Livramento");
                result.put("uf", "RS");
                result.put("latitude", -30.8925); // Coordenadas específicas de Santana do Livramento
                result.put("longitude", -55.5328);
                return result;
            }
            
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
                    
                    // Coordenadas aproximadas baseadas na cidade
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
        // Coordenadas simuladas para cidades principais
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
        cityCoordinates.put("Uruguaiana", -29.7614);
        cityCoordinates.put("Pelotas", -31.7719);
        cityCoordinates.put("Caxias do Sul", -29.1686);
        cityCoordinates.put("Passo Fundo", -28.2619);
        cityCoordinates.put("Santa Cruz do Sul", -29.7178);
        cityCoordinates.put("Canoas", -29.9178);
        cityCoordinates.put("Gravataí", -29.9428);
        cityCoordinates.put("Viamão", -30.0819);
        cityCoordinates.put("Novo Hamburgo", -29.6868);
        cityCoordinates.put("São Leopoldo", -29.7604);
        cityCoordinates.put("Alvorada", -29.9989);
        cityCoordinates.put("Cachoeirinha", -29.9508);
        cityCoordinates.put("Esteio", -29.8519);
        cityCoordinates.put("Sapucaia do Sul", -29.8419);
        cityCoordinates.put("Guaíba", -30.1139);
        cityCoordinates.put("Bento Gonçalves", -29.1686);
        cityCoordinates.put("Erechim", -27.6344);
        cityCoordinates.put("Bagé", -31.3314);
        cityCoordinates.put("Lajeado", -29.4669);
        cityCoordinates.put("Ijuí", -28.3878);
        cityCoordinates.put("Santiago", -29.1917);
        cityCoordinates.put("Alegrete", -29.7831);
        cityCoordinates.put("Santo Ângelo", -28.3008);
        cityCoordinates.put("Vacaria", -28.5122);
        cityCoordinates.put("Cruz Alta", -28.6442);
        cityCoordinates.put("Carazinho", -28.2839);
        cityCoordinates.put("Palmeira das Missões", -27.8994);
        cityCoordinates.put("Frederico Westphalen", -27.3589);
        cityCoordinates.put("São Borja", -28.6611);
        cityCoordinates.put("Dom Pedrito", -30.9833);
        cityCoordinates.put("Rosário do Sul", -30.2417);
        cityCoordinates.put("São Gabriel", -30.3333);
        cityCoordinates.put("Livramento", -30.8925);
        
        return cityCoordinates.getOrDefault(city, -29.6868); // Default para Santa Maria
    }
    
    private double getLongitudeByCity(String city) {
        // Coordenadas simuladas para cidades principais
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
        cityCoordinates.put("Uruguaiana", -57.0819);
        cityCoordinates.put("Pelotas", -52.3428);
        cityCoordinates.put("Caxias do Sul", -51.1794);
        cityCoordinates.put("Passo Fundo", -52.4069);
        cityCoordinates.put("Santa Cruz do Sul", -52.4258);
        cityCoordinates.put("Canoas", -51.1839);
        cityCoordinates.put("Gravataí", -50.9928);
        cityCoordinates.put("Viamão", -51.0239);
        cityCoordinates.put("Novo Hamburgo", -51.1289);
        cityCoordinates.put("São Leopoldo", -51.1478);
        cityCoordinates.put("Alvorada", -51.0308);
        cityCoordinates.put("Cachoeirinha", -51.0939);
        cityCoordinates.put("Esteio", -51.1508);
        cityCoordinates.put("Sapucaia do Sul", -51.1469);
        cityCoordinates.put("Guaíba", -51.3258);
        cityCoordinates.put("Bento Gonçalves", -51.5189);
        cityCoordinates.put("Erechim", -52.2756);
        cityCoordinates.put("Bagé", -54.1069);
        cityCoordinates.put("Lajeado", -51.9619);
        cityCoordinates.put("Ijuí", -53.9147);
        cityCoordinates.put("Santiago", -54.8672);
        cityCoordinates.put("Alegrete", -55.7919);
        cityCoordinates.put("Santo Ângelo", -54.2639);
        cityCoordinates.put("Vacaria", -50.9339);
        cityCoordinates.put("Cruz Alta", -53.6058);
        cityCoordinates.put("Carazinho", -52.7869);
        cityCoordinates.put("Palmeira das Missões", -53.3139);
        cityCoordinates.put("Frederico Westphalen", -53.3947);
        cityCoordinates.put("São Borja", -56.0044);
        cityCoordinates.put("Dom Pedrito", -54.6733);
        cityCoordinates.put("Rosário do Sul", -54.9133);
        cityCoordinates.put("São Gabriel", -54.3200);
        cityCoordinates.put("Livramento", -55.5328);
        
        return cityCoordinates.getOrDefault(city, -53.8149); // Default para Santa Maria
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
    
    private double calculateSimulatedDistanceByCep(String originCep, String destinationCep) {
        // Cálculo simulado baseado em diferenças de CEP
        String originUf = getRegionFromCep(originCep);
        String destinationUf = getRegionFromCep(destinationCep);
        
        if (originUf.equals(destinationUf)) {
            return Math.random() * 10 + 5; // 5-15 km no mesmo estado
        } else {
            return Math.random() * 100 + 50; // 50-150 km entre estados
        }
    }
    
    private String extractCep(String address) {
        if (address == null || address.trim().isEmpty()) {
            return null;
        }
        
        // Limpar a string de entrada
        String cleanAddress = address.trim();
        
        // Se já é um CEP válido (formato 00000-000 ou 00000000)
        if (cleanAddress.matches("\\d{5}-?\\d{3}")) {
            // Formatar para o padrão 00000-000
            String cepOnly = cleanAddress.replace("-", "");
            return cepOnly.substring(0, 5) + "-" + cepOnly.substring(5);
        }
        
        // Se é apenas números (8 dígitos)
        if (cleanAddress.matches("\\d{8}")) {
            return cleanAddress.substring(0, 5) + "-" + cleanAddress.substring(5);
        }
        
        // Extrair CEP do endereço usando regex
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d{5}-?\\d{3}");
        java.util.regex.Matcher matcher = pattern.matcher(cleanAddress);
        
        if (matcher.find()) {
            String foundCep = matcher.group();
            // Formatar para o padrão 00000-000
            String cepOnly = foundCep.replace("-", "");
            return cepOnly.substring(0, 5) + "-" + cepOnly.substring(5);
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