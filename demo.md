# Mémo de démo

### 0. Se placer dans le bon dossier
`cd 'C:\FakeD\Cours\Ingé\Ing3 S5\ORC\TP1\'`
### 1. Démarrer les applications :

**ms-membership / ms-product / ms-order :**
Depuis le dossier associé
- `mvn spring-boot:run`		*ou bien java -jar ./target/ms-membership-1.0.0-SNAPSHOT.jar (recip. ms-product et ms-order)*

**/!\ Démarrer Docker Desktop**

**monitoring :**
Depuis le dossier racine :
- `docker compose up -d`

**front :**
Depuis le dossier associé :
- `docker compose -f ../docker-compose.frontend.yml up --build`

### 2. Vérifier les interfaces web :

- Micro services : 	http://localhost:8081/actuator/health	(et 8082/8083)
- Prometheus UI : 	http://localhost:9090/targets		(attendre jusqu'à 30s)
- Grafana : 		http://localhost:3000/dashboards	(pas vraiment fonctionnel)
- React :		http://localhost:5000/			(globalement complet mais pas parfait)

### 3. Interraction avec les micros services :

**A. Via postman avec la collection fournie :**
- Executer les requetes que l'on souhaite 
  - créer un user
  - product
  - changer son stock (addition)
  - creer une commande
- Via l'interface zeb (idem)