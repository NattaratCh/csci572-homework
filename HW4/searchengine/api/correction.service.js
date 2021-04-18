var SpellCorrector = require('spelling-corrector');
var sc = new SpellCorrector();

const correction = (query) => {
    sc.loadDictionary()
    console.log(sc.correct(query));
}

module.exports = {
    correction
}