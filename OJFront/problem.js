var problemId = ""

function requestProblem() {
    $('#feedback-panel').removeClass('hidden');
    $('#feedback-text').text('Loading');
    $('#title-panel').addClass('hidden');
    $('#description-panel').addClass('hidden');
    $('#source-panel').addClass('hidden');
    $.get('judge/problem/' + problemId, function(data) {
        $('#title').text(data.title);
        $('#description').text(data.description);
        $('#source').text(data.template);
        $('#title-panel').removeClass('hidden');
        $('#description-panel').removeClass('hidden');
        $('#source-panel').removeClass('hidden');
        $('#problem-id-panel').addClass('hidden');
        $('pre code').each(function(i, block) {
            hljs.highlightBlock(block);
        });
    }).fail(function() {
        alert('Failed to request');
        $('#problem-id-panel').removeClass('hidden');
    }).always(function() {
        $('#feedback-panel').addClass('hidden');
    });
}

function detectProblem() {
    if (problemId == "") {
        $('#feedback-panel').addClass('hidden');
        $('#problem-id-panel').removeClass('hidden');
    } else {
        requestProblem();
    }
}

$(document).ready(function() {
    problemId = document.location.hash;
    detectProblem();
    window.onhashchange = function() {
        problemId = document.location.hash
        detectProblem();
    };
    $('#problem-id-enter').click(function() {
        document.location.hash = $('#problem-id').val();
    });
    $('#source-submit').click(function() {
        $.ajax('judge/submit', {
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                problemId: problemId,
                source: $('#source').text
            }),
            success: function(data) {
                window.location.href = 'record.html#' + data.recordId;
            },
            error: function() {
                alert("Failed to submit");
            }
        });
    });
    $('pre code').on('blur', function() {
        hljs.highlightBlock(this);
    });
});
