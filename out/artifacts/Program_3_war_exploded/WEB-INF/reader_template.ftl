<html>
<head>
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
<div id="link" style="color: black">${STORY}</div>
<div id="content" style="color: black">

</div>
<div id="footer">Zach Eldemire Reader</div>
</body>
</html>