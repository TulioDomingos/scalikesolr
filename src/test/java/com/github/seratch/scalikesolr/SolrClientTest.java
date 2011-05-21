package com.github.seratch.scalikesolr;

import com.github.seratch.scalikesolr.request.*;
import com.github.seratch.scalikesolr.request.common.WriterType;
import com.github.seratch.scalikesolr.request.query.Query;
import com.github.seratch.scalikesolr.request.query.Sort;
import com.github.seratch.scalikesolr.request.query.morelikethis.FieldsToUseForSimilarity;
import com.github.seratch.scalikesolr.request.query.morelikethis.MoreLikeThisParams;
import com.github.seratch.scalikesolr.response.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class SolrClientTest {

    Logger log = LoggerFactory.getLogger(this.getClass());

    SolrClient client;

    @Before
    public void setUp() throws Exception {
        client = Solr.httpServer(new URL("http://localhost:8983/solr")).getNewClient();
    }

    @Test
    public void doQuery() throws Exception {
        QueryRequest request = new QueryRequest(new Query("author:Rick"));
        QueryResponse response = client.doQuery(request);
        assertThat(response.getResponseHeader(), is(notNullValue()));
        assertThat(response.getResponse(), is(notNullValue()));
        assertThat(response.getHighlightings(), is(notNullValue()));
        assertThat(response.getMoreLikeThis(), is(notNullValue()));
        assertThat(response.getFacet(), is(notNullValue()));
        List<SolrDocument> documents = response.getResponse().getDocumentsInJava();
        for (SolrDocument document : documents) {
            log.debug(document.get("id").toString());
            log.debug(document.get("cat").toListInJavaOrElse(null).toString());
            log.debug(document.get("pages_i").toNullableIntOrElse(null).toString());
            log.debug(document.get("price").toNullableDoubleOrElse(null).toString());
        }
    }

    @Test
    public void doQuery_JSON() throws Exception {
        QueryRequest request = new QueryRequest(new Query("author:Rick"));
        request.setWriterType(WriterType.JSON());
        request.setSort(Sort.as("author desc"));
        request.setMoreLikeThis(MoreLikeThisParams.as(true, 3, FieldsToUseForSimilarity.as("title")));
        QueryResponse response = client.doQuery(request);
        assertThat(response.getResponseHeader(), is(notNullValue()));
        assertThat(response.getResponse(), is(notNullValue()));
        assertThat(response.getHighlightings(), is(notNullValue()));
        assertThat(response.getMoreLikeThis(), is(notNullValue()));
        assertThat(response.getFacet(), is(notNullValue()));
    }

//	@Test
//	public void doDIHCommand() throws Exception {
//		DIHCommandRequest request = new DIHCommandRequest("delta-import");
//		DIHCommandResponse response = client.doDIHCommand(request);
//		assertThat(response.getCommand(), is(notNullValue()));
//		assertThat(response.getImportResponse(), is(notNullValue()));
//		assertThat(response.getRawBody(), is(notNullValue()));
//		assertThat(response.getStatus(), is(notNullValue()));
//		assertThat(response.getStatusMessages().getDefaults(), is(notNullValue()));
//		assertThat(response.getInitArgs().getDefaults(), is(notNullValue()));
//	}

    @Test
    public void doDeleteDocuments() throws Exception {
        DeleteRequest request = new DeleteRequest();
        List<String> uniqueKeys = new ArrayList<String>();
        uniqueKeys.add("978-0641723445");
        request.setUniqueKeysToDetelteInJava(uniqueKeys);
        DeleteResponse response = client.doDeleteDocuments(request);
        assertThat(response.responseHeader(), is(notNullValue()));
        client.doCommit(new UpdateRequest());
    }

    @Test
    public void doAddDocuments() throws Exception {
        AddRequest request = new AddRequest();
        String jsonString = "{\"id\" : \"978-0641723445\", \"cat\" : [\"book\",\"hardcover\"], \"title\" : \"The Lightning Thief\", \"author\" : \"Rick Riordan\", \"series_t\" : \"Percy Jackson and the Olympians\", \"sequence_i\" : 1, \"genre_s\" : \"fantasy\", \"inStock\" : true, \"price\" : 12.50, \"pages_i\" : 384}";
        SolrDocument doc = new SolrDocument(WriterType.JSON(), jsonString);
        java.util.List<SolrDocument> docs = new ArrayList<SolrDocument>();
        docs.add(doc);
        request.setDocumentsInJava(docs);
        AddResponse response = client.doAddDocuments(request);
        assertThat(response.responseHeader(), is(notNullValue()));
        client.doCommit(new UpdateRequest());
    }

    @Test
    public void doCommit() throws Exception {
        UpdateRequest request = new UpdateRequest();
        UpdateResponse response = client.doCommit(request);
        assertThat(response.responseHeader(), is(notNullValue()));
    }

    @Test
    public void doRollback() throws Exception {
        UpdateRequest request = new UpdateRequest();
        UpdateResponse response = client.doRollback(request);
        assertThat(response.responseHeader(), is(notNullValue()));
    }

    @Test
    public void doPing() throws Exception {
        PingRequest request = new PingRequest();
        PingResponse response = client.doPing(request);
        assertThat(response.responseHeader(), is(notNullValue()));
    }


}
