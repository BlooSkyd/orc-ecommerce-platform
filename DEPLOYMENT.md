- Prérequis (Java, Maven, Docker, etc.)
- Instructions de démarrage pas-à-pas :
  1. Cloner le repository
  2. Compiler chaque service
  3. Lancer dans le bon ordre
      - `./ms-membership` : `mvn install`
      - `./ms-product` : `mvn install`
      - `./ms-order` : `mvn install`
      - `.` : `docker compose up -d` 
  4. Vérifier que tout fonctionne
- Configuration des ports
- Variables d'environnement
- Troubleshooting courant