const searchService = require('./search.service')

const search = async (request, response) => {
    const query = request.query.query
    const pageRankEnabledStr = request.query.pageRankEnabled

    console.log(request)

    if (query === undefined) {
        response.status(400).send({
            success: false,
            message: "Invalid parameters"
        })
    } else {
        // TODO handle if url is null
        const pageRankEnabled = pageRankEnabledStr === 'true'
        searchService.getSearchResults(query, pageRankEnabled)
        .then(res => {
            response.status(200).send({
                success: true,
                data: res.response
            })
        })
        .catch(err => {
            response.status(400).send({
                success: false,
                message: err
            })
        })
    }
}

module.exports = {
    search
}