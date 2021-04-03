const axios = require('axios');

const getSearchResults = (query, pageRankEnabled) => {
    const solrBaseURL = 'http://localhost:8983/solr/myexample/select'
    const queryParams = '?fl=' + encodeURIComponent('title,og_url,id,og_description') + '&q=' + encodeURIComponent(query)
    const pageRankParam = '&sort=' + encodeURIComponent('pageRankFile desc')
    const url = solrBaseURL + queryParams + (pageRankEnabled ? pageRankParam : '')

    const promise = axios.get(url).then(res => res.data)
    return promise
}

module.exports = {
    getSearchResults
}