const correctionService = require('./correction.service')

const correction = async (request, response) => {
    const query = request.query.query
    let correctWord = correctionService.correction(query)
    response.status(200).send({
        success: true,
        data: correctWord
    })
}

module.exports = {
    correction
}