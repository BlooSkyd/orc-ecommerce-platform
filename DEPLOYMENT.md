# Guide de déploiement - Plateforme E‑Commerce (TP1)

## Prérequis

- Java 21 (OpenJDK)
- Maven 3.6+ (sous java 21 aussi, vérifiable via `mvn -version`)
- Docker Desktop (ou Docker engine) / docker et docker-compose
- Postman pour tests
- Node (npm) 10.9+

## Ports attendus

- ms-membership : 8081
- ms-product : 8082
- ms-order : 8083
- Prometheus : 9090
- Grafana : 3000
- ms-front : 5000

(pas d'api gateway)

## Cloner le repository

```shell
git clone https://github.com/BlooSkyd/orc-ecommerce-platform.git
```

## Compilation

Depuis la racine du projet (où se trouvent les dossiers `ms-*`):

**/!\ Ordre important à respecter /!\\**
PowerShell (Windows) / bash (UNIX) :

```powershell
# --- Installation ms-membership
cd .\ms-membership
mvn clean install
# mvn clean install -DskipTests pour passer les tests unitaires

# --- Installation ms-product
cd ..\ms-product
mvn clean install

# --- Installation ms-order
cd ..\ms-order
mvn clean install

# --- Installation ms-front
cd ..\ms-front
npm install
```


Les jars seront disponibles dans `*/target/*.jar`.

## Démarrage local

**Avec plusieurs terminaux (pour voir les logs en synchrone) :**
Lancer chaque service (ex. PowerShell) :

```powershell
# Membership
cd .\ms-membership
mvn spring-boot:run
# ou bien java -jar .\target\ms-membership-1.0.0-SNAPSHOT.jar
```
```
# Product
cd ..\ms-product
mvn spring-boot:run
# ou java -jar...
```
```
# Order
cd ..\ms-order
mvn spring-boot:run
# ou java -jar...
```
```
# Front
cd \ms-front
docker compose -f ../docker-compose.frontend.yml up --build
```

```
# Monitoring
cd \monitoring
docker compose up -d
```

Chaque service expose `/actuator/health` et `/actuator/prometheus`.

**Pour arrêter les services :**
- `Ctrl+C` pour les tous les `ms-*` (front inclus)
- `docker compose stop` pour le monitoring

## Vérifications post-démarrage

- Accéder à Health : `http://localhost:8081/actuator/health` (et 8082/8083)
- Prometheus UI : `http://localhost:9090/targets`  `Accueil > Status > Targets` pour vérifier les targets UP (attendre jusqu'à 30s)
- Grafana : `http://localhost:3000/dashboards` (admin/admin) — dashboards **(pas vraiment fonctionnels)**

## Scénario de test rapide (happy-path)

**I. Via Postman :**
- Utiliser la collection Postman fournie : `postman/ORC-TP1-Clement-TAURAND.postman_collection.json`.
- Ne pas oublier d'importer la config des variables d'environnement pour postman située dans le meme dossier
- Dérouler chaque requête une par une depuis le dossier `Scénarios\Scénario complet`
- OU : Jouer les requêtes de notre choix depuis les dossiers `Products`, `Users` ou `Orders`

**II. À la main (interface web)**
1. Créer un utilisateur via la rubrique **Users** (`New user` en haut à droite) (`New user` en haut à droite)
2. Créer quelques produits via la rubrique **Products** (`New product` en haut à droite), jouer avec les paramètres.
   - ***Attention :** Le front ayant été fait en dernier minute, il est possible de rentrer des données qui seront refusées par le service.*
3. Créer une commande la rubrique **Orders** (`New order` en haut à droite) (vérifier déduction stock)
   - *Après la création d'une commande, elle apparaît en bas de la liste, mais il faut rafraîchir la page pour pouvoir afficher ses détails correctement.*

## Troubleshooting courant

- Services non joignables : vérifier `docker-compose ps` et `docker-compose logs <service>`.
- Erreur `Connection refused` depuis Prometheus : vérifier targets dans `prometheus.yml` et adapter `host.docker.internal` vs `service-name`.
- Problèmes de port : vérifier qu'aucun autre processus n'écoute les ports 8081/8082/8083.
- Tests d'endpoint :
  - `curl http://localhost:8082/api/v1/products` pour vérifier product API
  - `curl http://localhost:8082/actuator/prometheus` pour les métriques