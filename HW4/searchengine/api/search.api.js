const searchService = require('./search.service')
const fs = require('fs')
const csv = require('csv-parser')
const { response } = require('../app')
let urlMapping = {}

const loadUrlMapping = () => {
    fs.createReadStream(__dirname + '/URLtoHTML_nytimes_news.csv')
    .pipe(csv())
    .on('data', (row) => {
        urlMapping[row.filename] = row.URL
    })
    .on('end', () => {
        console.log('CSV file successfully processed');
    });
}

const search = async (request, response) => {
    const query = request.query.query
    const rankType = request.query.rankType

    if (query === undefined) {
        response.status(400).send({
            success: false,
            message: "Invalid parameters"
        })
    } else {
        const pageRankEnabled = rankType === 'PageRank'
        searchService.getSearchResults(query, pageRankEnabled)
        .then(res => {
            res.response.docs.map(function (e) {
                if (e.og_url === undefined || e.og_url === null) {
                    let idx = e.id.lastIndexOf('/')
                    let filename = e.id.substring(idx+1)
                    e.og_url = urlMapping[filename]
                }

                if (e.og_description === undefined || e.og_description === null) {
                    e.og_description = 'N/A'
                }
            })

            response.status(200).send({
                success: true,
                data: res.response
            })
        })
        .catch(err => {
            console.log(err)
            response.status(400).send({
                success: false,
                message: err
            })
        })
    }
}

const suggest = async (request, response) => {
    const query = request.query.query

    if (query === undefined) {
        response.status(400).send({
            success: false,
            message: "Invalid parameters"
        })
    } else {
        searchService.getSuggestion(query)
        .then(res => {
            let suggestions = res.suggest.suggest[query].suggestions
            response.status(200).send({
                success: true,
                data: suggestions.map(x => x.term)
            })
        })
        .catch(err => {
            console.log(err)
            response.status(400).send({
                success: false,
                message: err
            })
        })
    }
}

module.exports = {
    search,
    loadUrlMapping,
    suggest
}