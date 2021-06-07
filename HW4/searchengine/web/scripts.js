const baseUrl = 'http://localhost:3000/api/'

$(document).ready(function(){
    $("form").submit(function(event) {
        event.preventDefault()
        search()
    })

    $('#query').on('input', function() {
        let query = $("#query").val()
        suggest(query)
    })
})

function suggest(query) {
    let url = baseUrl + "/suggest?query=" + query
    $.get(url, function(response, status) {
        let results = response.data
        renderSuggestion(results)
    })
    .fail(function(jqXHR, textStatus, error) {
        alert(jqXHR.responseJSON.message)
    })
}

function renderSuggestion(suggestions) {
    let html = ''
    $.each(suggestions, function(index, data) {
        console.log(data)
        html += '<div class="suggestion">' + data + '</div>'
    })
    $('.autocomplete-list').html(html)

    $(document).on('click', '.suggestion', function(e) {
        e.preventDefault();
        $("#query").val($(this).text())
        search()
    })
}

function clearSuggestion() {
    $('.autocomplete-list').html('')
}

function search() {
    clearCorrection()
    clearSuggestion()
    var values = {};
    let queryString = ''
    $.each($('#search-form').serializeArray(), function(i, field) {
        values[field.name] = field.value;
    });

    $.each(values, function(key, value) {
        if (value !== '') {
            if (queryString !== '') queryString += '&'
            queryString += key + '=' + value
        }
        })
    solrRequest(queryString)
}

function solrRequest(queryString) {
    let url = baseUrl + '/search?' + queryString
    $.get(url, function(data, status) {
        let results = data.data.docs
        renderSearchResults(results)
        renderTotalResuls(data.data.numFound, results.length)
        if (data.correction !== null) {
            renderCorrection(data.correction)
        }
    })
    .fail(function(jqXHR, textStatus, error) {
        alert(jqXHR.responseJSON.message)
    })
}

function renderCorrection(correctWord) {
    let html = 'Did you mean '
    html += '<span class="correct-word">' + correctWord + '</span>?'
    $("#correction").html(html)

    $(document).on('click', '.correct-word', function(e) {
        e.preventDefault();
        $("#query").val($(this).text())
        search()
    })
}

function clearCorrection() {
    $("#correction").html('')
}

function renderTotalResuls(numFound, docsSize) {
    let html = 'Record(s): ' + docsSize + ' of ' + numFound
    $('#totalResults').html(html)
}

function renderSearchResults(results) {
    let html = ''
    if (results === undefined || results.length === 0) {
        html += '<div class="card mb-3">'
        html += '   <div class="card-body text-center">'
        html += '       No information found.'
        html += '   </div>'
        html += '</div>'
    } else {
        $.each(results, function(index, data) {
            html += '<div class="card mb-3">'
            html += '   <div class="card-body">'
            html += '       <div><label>Title: </label> <a href="' + data.og_url + '" target="_blank">' + data.title + '</a></div>'
            html += '       <div><label>URL: </label> <a href="' + data.og_url + '" target="_blank">' + data.og_url + '</a></div>'
            html += '       <div><label>Description: </label> <span>' + (data.og_description === undefined || data.og_description === '' ? 'N/A' : data.og_description) + '</span></div>'
            html += '       <div><label>ID: </label> ' + data.id + '</div>'
            html += '   </div>'
            html += '</div>'
        })
    }

    $('#result').html(html)
}