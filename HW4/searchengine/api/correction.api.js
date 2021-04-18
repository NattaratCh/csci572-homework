const correctionService = require('./correction.service')

const correction = async (request, response) => {
    const query = request.query.query
    correctionService.correction(query)
    response.status(200).send({
        success: true
    })
}

module.exports = {
    correction
}