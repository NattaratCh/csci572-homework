const express = require("express");
const searchApi = require("../api/search.api")
const router = express.Router();

router.get("/search", searchApi.search)

module.exports = router