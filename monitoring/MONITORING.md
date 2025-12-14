# Monitoring (Prometheus + Grafana)

Ce dossier contient la configuration minimale pour lancer Prometheus (port 9090) et Grafana (port 3000) en local via Docker Compose.

Contenu ajouté :
- `prometheus.yml` : configuration de scraping
- `docker-compose.yml` : lance Prometheus et Grafana

But : Prometheus va scrapper les endpoints prometheus exposés par vos microservices Spring Boot (endpoint attendu : `/actuator/prometheus`).

Comment ça marche (résumé) :
- Prometheus est un collecteur (pull model) : il interroge périodiquement (`scrape_interval`) les endpoints configurés.
- Chaque application Spring Boot doit exposer ses métriques sur `/actuator/prometheus` (Actuator + micrometer prometheus).
- Grafana se connecte à Prometheus comme datasource pour afficher des tableaux de bord.

Points importants pour vos microservices Spring Boot :
- Activer l'exposition de l'endpoint prometheus dans `application.yml` :

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,metrics
  endpoint:
    prometheus:
      enabled: true
```

- Vérifier que chaque service écoute bien sur les ports attendus (membership:8081, product:8082, order:8083).

Lancer Prometheus et Grafana (PowerShell) :

```powershell
# depuis la racine du projet (où se trouve docker-compose.yml)
docker-compose up -d

# voir les containers
docker-compose ps

# logs (Prometheus)
docker-compose logs -f prometheus
```

Vérifications 
- Accéder à l'interface Prometheus : http://localhost:9090
  - Page des targets : http://localhost:9090/targets (vérifier que vos services sont UP)
- Accéder à Grafana : http://localhost:3000 (login par défaut admin / admin). Dans Grafana, ajouter une datasource Prometheus :
  - URL : `http://prometheus:9090` (Grafana dans le même docker-compose peut accéder au service par nom)

Dashboards fournis par défaut
- `monitoring/grafana/dashboards/orders-dashboard.json` : Dashboard métiers pour les commandes (montant du jour, taux par statut)
- `monitoring/grafana/dashboards/products-dashboard.json` : Dashboard métiers pour les produits (créations, mises à jour, targets up)

Ces dashboards sont provisionnés automatiquement par Grafana au démarrage grâce aux fichiers dans `monitoring/grafana/provisioning/`.

Health check et métrique ajoutés
- Health check personnalisé `LowStockHealthIndicator` exposé via `/actuator/health` dans le service `ms-product` (DOWN si il y a des produits avec stock < 5).
- Métrique Micrometer `products.low_stock.count` exposée via `/actuator/prometheus` pour que Prometheus la scrappe et Grafana l'affiche.

Fichiers ajoutés dans le service `ms-product`:
- `src/main/java/com/product/products/infrastructure/health/LowStockHealthIndicator.java`
- `src/main/java/com/product/products/infrastructure/metrics/LowStockMetrics.java`

Dashboard mis à jour:
- `monitoring/grafana/dashboards/products-dashboard.json` inclut maintenant un panneau "Produits avec stock bas (<5)" basé sur `products.low_stock.count`.

Dépannage courant :
- Si vos services ne sont pas visibles : depuis la machine hôte faites `curl http://localhost:8081/actuator/prometheus` pour vérifier l'endpoint.
- Sous Windows + Docker Desktop, `host.docker.internal` est utilisé dans `prometheus.yml` pour que Prometheus (dans un container) atteigne les services en écoute sur l'hôte.
- Si vous exécutez les microservices dans des containers Docker du même réseau compose, vous pouvez remplacer les targets par `service-name:port`.

Bonnes pratiques / extension
- Ajouter des règles d'alerting et des dashboards Grafana (import JSON).
- Configurer Prometheus pour persister les données sur un volume (déjà préparé dans `docker-compose.yml`).

Fichiers créés par ce script :
- `docker-compose.yml` (à la racine)
- `monitoring/prometheus.yml`
- `monitoring/MONITORING.md`
