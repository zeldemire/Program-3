<html>
<head>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>
    <title>${TITLE}</title>
    <style>
        body {
            margin: auto;
            width: 100vw;
            height: 100vh;
            display: flex;
            flex-flow: row wrap;
            align-content: flex-start;
            background-color: #252729;
        }

        #header {
            color: white;
            text-align: left;
            padding: 5px;
            width: 100vw;
        }

        #link {
            line-height: 30px;
            background-color: #66686a;
            width: 20%;
            height: 93vh;
        }

        #content {
            background-color: #a8aaac;
            width: 80%;
            height: 93vh;

        }

        #footer {
            color: white;
            text-align: left;
            padding: 5px;
            width: 100vw;
        }
    </style>
</head>
<body>
<div id="header">Welcome ${USER} ${EMAIL}</div>
<div id="link" style="color: black">
    <button id="prev">Previous page</button>
    <button id="next">Next page</button>
    <#--Add a home button to take you back to story selection-->
</div>
<div id="content" style="color: black">
    <div id="divs">
    <#list PAGE as PAGES>
        <p>${PAGES}</p>
        <#else>
        <p>Story Doesn't Exist.</p>
    </#list>
    </div>
</div>
<div id="footer">Zach Eldemire Reader</div>

<script>
    $(document).ready(function(){
        $("#divs p").each(function(e) {
            if (e != 0)
                $(this).hide();
        });
        var len = $("#divs p").length;
        var count = 1;
        $('#prev').hide();
        if(len == 1)
            $('#next').hide();
        $("#next").click(function(){
            count++;
            if (count == len)
                $('#next').hide();
            if ($("#divs p:visible").next().length != 0)
                $("#divs p:visible").next().show().prev().hide();

            else {
                $("#divs p:visible").hide();
                $("#divs p:first").show();

            }
            $('#prev').show();
            return false;
        });

        $("#prev").click(function(){
            $('#next').show();
            count--;
            if(count == 1)
                $('#prev').hide();
            if ($("#divs p:visible").prev().length != 0)
                $("#divs p:visible").prev().show().next().hide();
            else {
                $("#divs p:visible").hide();
                $("#divs p:last").show();
            }
            return false;
        });
    });
</script>
</body>
</html>