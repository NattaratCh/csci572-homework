var SpellCorrector = require('spelling-corrector');
var sc = new SpellCorrector();
sc.loadDictionary(__dirname + '/big.txt')

const correction = (query) => {
    console.log(sc.correct(query))
    return sc.correct(query)
}

module.exports = {
    correction
}