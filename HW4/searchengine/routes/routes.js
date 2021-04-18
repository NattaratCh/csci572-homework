const express = require("express");
const searchApi = require("../api/search.api")
const correctionApi = require("../api/correction.api")
const router = express.Router();

router.get("/search", searchApi.search)
router.get("/suggest", searchApi.suggest)
router.get("/correct", correctionApi.correction)

module.exports = router