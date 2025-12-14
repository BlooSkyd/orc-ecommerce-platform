# Document d'Architecture Technique

## Vue d'ensemble

## Schéma d'architecture (diagramme)

## Description de chaque microservice

### A. MS-USER

Pratiquement aucuns changements n'ont été oppéré sur ce service.
Les seuls certains sont :
- `resources/application.yml`: ajout du nom du service dans les logs* 
- `resources/data.sql` : ajout d'objets afin de remplir la base de données
- `configuration/IdentityInitializer.java` : ajout permettant de continuer à sauvegarder de nouveaux objets en base après l'ajout de données via le script `data.sql`*
- `configuration/MetricsInitializer.java` : ajout afin de mettre à jour les metrics micrometer une fois l'application démarrée pour prendre en comptes les données créées via le script `data.sql`

\* ajouts faits sur tous les services

### B. MS-PRODUCT

### C. MS-ORDER

## Choix technologiques justifiés

## Stratégie de communication inter-services

## Gestion des données (base de données par service)

## Gestion des erreurs et résilience