package com.totalelectro.service;

public interface ShippingService {
    
    /**
     * Calcula o frete baseado no CEP de destino. O CEP de origem é o da loja.
     * @param destinationCep CEP de destino do cliente.
     * @return O valor do frete calculado.
     */
    double calculateShipping(String destinationCep);
    
    /**
     * Calcula o frete baseado na distância em quilômetros
     * @param distanceInKm Distância em quilômetros
     * @return Valor do frete calculado
     */
    double calculateShippingByDistance(double distanceInKm);
    
    /**
     * Obtém a distância em Km baseado no CEP de destino. O CEP de origem é o da loja.
     * @param destinationCep O CEP de destino.
     * @return A distância em quilômetros.
     */
    double getDistance(String destinationCep);
    
    /**
     * Obtém a taxa base do frete
     * @return Taxa base em reais
     */
    double getBaseRate();
    
    /**
     * Obtém a taxa por quilômetro
     * @return Taxa por km em reais
     */
    double getRatePerKm();
    
    /**
     * Obtém o valor mínimo para frete grátis
     * @return Valor mínimo em reais
     */
    double getFreeShippingThreshold();
    
    /**
     * Obtém a distância máxima para entrega
     * @return Distância máxima em quilômetros
     */
    double getMaxDistance();

    /**
     * Obtém o CEP da loja.
     * @return O CEP da loja.
     */
    String getStoreCep();
} 