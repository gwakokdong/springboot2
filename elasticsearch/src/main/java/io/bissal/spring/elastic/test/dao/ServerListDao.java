package io.bissal.spring.elastic.test.dao;

import io.bissal.spring.elastic.test.component.ElasticRestClient;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ServerListDao {
    public static final String TERM_NAME_SERVERS = "each-server";

    @Autowired
    private ElasticRestClient client;

    public List<String> searchEachServers() {
        TermsAggregationBuilder termsAggregation
                = AggregationBuilders
                .terms(TERM_NAME_SERVERS)
                .field("host.id");

        SearchSourceBuilder search = new SearchSourceBuilder();
        search.aggregation(termsAggregation);
        search.fetchSource(false);
        search.size(0);

        List<String> list = searchTerms(search, TERM_NAME_SERVERS);

        return list;
    }

    private List<String> searchTerms(SearchSourceBuilder search, String termNameServers) {
        SearchRequest request = new SearchRequest("metricbeat-*");
        request.source(search);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        Terms terms = response.getAggregations().get(termNameServers);
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        List<String> list = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {
            list.add(bucket.getKeyAsString());
        }
        return list;
    }


}
