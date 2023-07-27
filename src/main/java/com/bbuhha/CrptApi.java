package com.bbuhha;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class CrptApi {
    private final TimeUnit timeUnit;
    private final int requestLimit;
    private final String url;
    private final BlockingQueue<Instant> requestQueue;

    public CrptApi(TimeUnit timeUnit, int requestLimit, String url) {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        this.url = url;
        this.requestQueue = new LinkedBlockingQueue<>(requestLimit);
    }

    public void doRequest(Document document, String signature) throws URISyntaxException, IOException, InterruptedException {
        Instant now = Instant.now();
        while (!requestQueue.isEmpty() && requestQueue.peek().isBefore(now.minusMillis(timeUnit.toMillis(1)))) {
            requestQueue.poll();
        }
        if (requestQueue.size() >= requestLimit) {
            Instant nextRequestTime = requestQueue.peek().plusMillis(timeUnit.toMillis(1));
            long waitTime = nextRequestTime.toEpochMilli() - now.toEpochMilli();
            Thread.sleep(waitTime);
        }

        RequestDto requestDto = new RequestDto(document, signature);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(this.url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestDto.toString()))
                .GET()
                .build();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        requestQueue.offer(Instant.now());
    }

    public class Description {
        private String participantInn;

        public Description(String participantInn) {
            this.participantInn = participantInn;
        }

        public String getParticipantInn() {
            return participantInn;
        }
    }
    public class Product {
        private String certificate_document;
        private Date certificate_document_date;
        private String certificate_document_number;
        private String owner_inn;
        private String producer_inn;
        private Date production_date;
        private String tnved_code;
        private String uit_code;
        private String uitu_code;

        public Product(String certificate_document,
                       Date certificate_document_date,
                       String certificate_document_number,
                       String owner_inn,
                       String producer_inn,
                       Date production_date,
                       String tnved_code,
                       String uit_code,
                       String uitu_code) {
            this.certificate_document = certificate_document;
            this.certificate_document_date = certificate_document_date;
            this.certificate_document_number = certificate_document_number;
            this.owner_inn = owner_inn;
            this.producer_inn = producer_inn;
            this.production_date = production_date;
            this.tnved_code = tnved_code;
            this.uit_code = uit_code;
            this.uitu_code = uitu_code;
        }

        public String getCertificate_document() {
            return certificate_document;
        }

        public Date getCertificate_document_date() {
            return certificate_document_date;
        }

        public String getCertificate_document_number() {
            return certificate_document_number;
        }

        public String getOwner_inn() {
            return owner_inn;
        }

        public String getProducer_inn() {
            return producer_inn;
        }

        public Date getProduction_date() {
            return production_date;
        }

        public String getTnved_code() {
            return tnved_code;
        }

        public String getUit_code() {
            return uit_code;
        }

        public String getUitu_code() {
            return uitu_code;
        }
    }
    public class Document {
        private Description description;
        private String doc_id;
        private String doc_status;
        private String doc_type;
        private Boolean importRequest = true;
        private String owner_inn;
        private String participant_inn;
        private String producer_inn;
        private Date production_date;
        private String production_type;
        private List<Product> products;
        private Date reg_date;
        private String reg_number;

        public Document(Description description,
                        String doc_id,
                        String doc_status,
                        String doc_type,
                        Boolean importRequest,
                        String owner_inn,
                        String participant_inn,
                        String producer_inn,
                        Date production_date,
                        String production_type,
                        List<Product> products,
                        Date reg_date,
                        String reg_number) {
            this.description = description;
            this.doc_id = doc_id;
            this.doc_status = doc_status;
            this.doc_type = doc_type;
            this.importRequest = importRequest;
            this.owner_inn = owner_inn;
            this.participant_inn = participant_inn;
            this.producer_inn = producer_inn;
            this.production_date = production_date;
            this.production_type = production_type;
            this.products = products;
            this.reg_date = reg_date;
            this.reg_number = reg_number;
        }

        public Description getDescription() {
            return description;
        }

        public String getDoc_id() {
            return doc_id;
        }

        public String getDoc_status() {
            return doc_status;
        }

        public String getDoc_type() {
            return doc_type;
        }

        public Boolean getImportRequest() {
            return importRequest;
        }

        public String getOwner_inn() {
            return owner_inn;
        }

        public String getParticipant_inn() {
            return participant_inn;
        }

        public String getProducer_inn() {
            return producer_inn;
        }

        public Date getProduction_date() {
            return production_date;
        }

        public String getProduction_type() {
            return production_type;
        }

        public List<Product> getProducts() {
            return products;
        }

        public Date getReg_date() {
            return reg_date;
        }

        public String getReg_number() {
            return reg_number;
        }
    }
    public class RequestDto {
        private Document document;
        private String signature;

        public RequestDto(Document document, String signature) {
            this.document = document;
            this.signature = signature;
        }

        public Document getDocument() {
            return document;
        }
        public String getSignature() {
            return signature;
        }
    }
}
