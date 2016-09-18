var recordId = ""

function requestRecord() {
    $('#record-id-panel').addClass('hidden');
    $('#feedback-panel').removeClass('hidden');
    $('#feedback-text').text('Loading');
    $('#info-panel').addClass('hidden');
    $('#source-panel').addClass('hidden');
    $.get('api/judge/record/' + recordId, function(data) {
        var date = new Date(data.timestamp * 1000);
        var dateString = date.getFullYear() + '/' + (date.getMonth() + 1) + '/' + date.getDate() + ' ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds()
        if (data.status == 'judging') {
            $('#info').text('Status: Judging\nDate: ' + dateString);
        } else if (data.status == 'canceled') {
            $('#info').text('Status: Canceled\nNote: Please re-submit your source code\nDate: ' + dateString);
        } else if (data.status == 'pending') {
            $('#info').text('Status: Pending\nQueue: ' + data.queue + '\nDate: ' + dateString);
        } else if (data.status == 'judged') {
            var infoString = '';
            infoString += 'Status: Judged\n';
            infoString += 'Result: ' + data.result + '\n';
            if (data.message != null) {
                infoString += 'Message:\n' + data.message + '\n';
            }
            if (data.compilerError != null && data.compilerError != '') {
                infoString += 'Compiler Error:\n' + data.compilerError + '\n';
            }
            if (data.input != null) {
                infoString += 'Input:\n' + data.input + '\n';
            }
            if (data.output != null) {
                infoString += 'Output:\n' + data.output + '\n';
            }
            if (data.expect != null) {
                infoString += 'Excepted Output:\n' + data.expect + '\n';
            }
            $('#info').text(infoString + dateString);
            $('#source').text(data.source);
            $('#source-panel').removeClass('hidden');
            $('pre code').each(function(i, block) {
                hljs.highlightBlock(block);
            });
        }
        $('#info-panel').removeClass('hidden');
    }).fail(function() {
        alert('Failed to request');
        $('#record-id-panel').removeClass('hidden');
    }).always(function() {
        $('#feedback-panel').addClass('hidden');
    });
}

function detectRecord() {
    if (recordId == "") {
        $('#feedback-panel').addClass('hidden');
        $('#record-id-panel').removeClass('hidden');
    } else {
        requestRecord();
    }
}

$(document).ready(function() {
    recordId = document.location.hash.slice(1);
    detectRecord();
    window.onhashchange = function() {
        recordId = document.location.hash
        detectRecord();
    };
    $('#record-id-enter').click(function() {
        document.location.hash = $('#record-id').val().slice(1);
    });
});
