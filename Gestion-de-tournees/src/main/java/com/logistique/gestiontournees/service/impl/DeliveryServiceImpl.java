package com.logistique.gestiontournees.service.impl;

import com.logistique.gestiontournees.dto.DeliveryDTO;
import com.logistique.gestiontournees.dto.DeliveryDistanceDTO;
import com.logistique.gestiontournees.entity.Delivery;
import com.logistique.gestiontournees.entity.Tour;
import com.logistique.gestiontournees.repository.DeliveryRepository;
import com.logistique.gestiontournees.repository.TourRepository;
import com.logistique.gestiontournees.service.DeliveryService;
import com.logistique.gestiontournees.service.mapper.DeliveryMapper;
import com.logistique.gestiontournees.util.DistanceCalculator;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DeliveryServiceImpl implements DeliveryService {
    private final  DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final TourRepository tourRepository;

    public  DeliveryServiceImpl(DeliveryRepository deliveryRepository, DeliveryMapper deliveryMapper, TourRepository tourRepository) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryMapper = deliveryMapper;
        this.tourRepository = tourRepository;

    }

    @Override
    public DeliveryDTO save(DeliveryDTO deliveryDTO) {
        // 1. Mapper le DTO en entité
        Delivery delivery = deliveryMapper.toEntity(deliveryDTO);

        // 2. Vérifier si un tourId est fourni
        if (deliveryDTO.getTourId() != null) {

            // 3. Récupérer l'entité Tour "parent"
            Tour tour = tourRepository.findById(deliveryDTO.getTourId())
                    .orElseThrow(() -> new EntityNotFoundException("Tournée non trouvée avec l'ID : " + deliveryDTO.getTourId()));

            // 4. GÉRER LES DEUX CÔTÉS DE LA RELATION

            // Côté "Enfant" (Delivery)
            delivery.setTour(tour);

            // Côté "Parent" (Tour)
            // C'est cette ligne qui corrige votre erreur @OrderColumn
            tour.getDeliveries().add(delivery);

            // Note : On n'a pas besoin de faire tourRepository.save(tour)
            // car la relation est gérée par le "côté" propriétaire (@ManyToOne)
            // et/ou la cascade. Sauvegarder l'enfant suffit.
        }

        // 5. Sauvegarder l'entité Delivery (qui va maintenant mettre à jour l'index)
        delivery = deliveryRepository.save(delivery);

        // 6. Retourner le DTO
        return deliveryMapper.toDto(delivery);
    }

    @Override
    public List<DeliveryDTO> findAll(){
        return deliveryRepository.findAll().stream()
                .map(deliveryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DeliveryDTO>findById(Long id){
        return deliveryRepository.findById(id).map(deliveryMapper::toDto);
    }

    @Override
    public void deleteById(Long id){
        deliveryRepository.deleteById(id);
    }

    @Override
    public List<DeliveryDistanceDTO> getDistancesFromDelivery(Long sourceDeliveryId) {
        Delivery sourceDelivery = deliveryRepository.findById(sourceDeliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Livraison source non trouvée avec l'ID : " + sourceDeliveryId));

        List<Delivery> allDeliveries = deliveryRepository.findAll();

        return allDeliveries.stream()
                .filter(targetDelivery -> !targetDelivery.getId().equals(sourceDeliveryId))
                .map(targetDelivery -> {

                    double distance = DistanceCalculator.calculateDistance(
                            sourceDelivery.getLatitude(), sourceDelivery.getLongitude(),
                            targetDelivery.getLatitude(), targetDelivery.getLongitude()
                    );

                    DeliveryDistanceDTO dto = new DeliveryDistanceDTO();
                    dto.setTargetDeliveryId(targetDelivery.getId());
                    dto.setTargetDeliveryAddress(targetDelivery.getAddress());
                    dto.setDistanceKm(distance);

                    return dto;
                })
                .collect(Collectors.toList());
    }

}
