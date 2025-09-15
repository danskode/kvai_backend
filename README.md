# KVAI – Kandidatvalg AI  

Dette projekt består af en **frontend** (plain JavaScript) og en **backend** (Java/Spring Boot), som tilsammen danner en platform, hvor vælgere kan chatte med digitale tvillinger af kandidaterne til det kommende lokalvalg i Danmark #kv25.  
Målet er at gøre det lettere for vælgere at lære kandidaternes holdninger at kende gennem en interaktiv samtaleoplevelse baseret på OpenAI’s sprogmodeller.  

## Repos  
- 👉 **Frontend (JavaScript/React):** [kvai-frontend](https://github.com/danskode/kvai-frontend)  
- 👉 **Backend (Java/Spring Boot):** [kvai_backend](https://github.com/danskode/kvai_backend)  

> ℹ️ Hvis du læser dette i **frontend-repoet**, finder du backend-koden [her](https://github.com/danskode/kvai_backend).  
> Hvis du læser dette i **backend-repoet**, finder du frontend-koden [her](https://github.com/danskode/kvai-frontend).  

## Funktioner  
- Realtids chat med kandidat-AI’er  
- Integration med OpenAI API  
- Skalerbar backend til håndtering af chatforespørgsler  
- Moderne, brugervenlig React-frontend  

## Teknologistak  
- **Frontend:** React, Vite/CRA, TailwindCSS (hvis relevant), WebSocket/HTTP til kommunikation  
- **Backend:** Java, Spring Boot, REST API, OpenAI SDK  
- **Andet:** Docker (valgfrit), PostgreSQL/MySQL (hvis database er i brug)  

## Kom i gang  

### Krav  
- Node.js (v18+)  
- Java 17+  
- OpenAI API key  

### Opsætning – Backend  
```bash
git clone https://github.com/danskode/kvai_backend.git
cd kvai_backend
./mvnw spring-boot:run
````

### Opsætning – Frontend

Frontend kører som udgangspunkt på `http://localhost:3000/`, mens backend kører på `http://localhost:8080/`.

## Konfiguration

Tilføj din OpenAI API-nøgle i backendens `application.properties` eller via miljøvariabel:

```properties
openai.api.key=din_api_nøgle
```

## Roadmap

* [ ] Kandidatprofiler med tilknyttede AI-personligheder
* [ ] Bedre brugergrænseflade til chatoplevelsen
* [ ] Mulighed for at filtrere kandidater efter kommune eller parti
* [ ] Statistik over interaktioner

## Bidrag

Pull requests og forslag er meget velkomne.
Opret et issue, hvis du finder en fejl eller har en idé til forbedring.

## Licens

Dette projekt er open source (MIT-licens). Fyr den af ...
