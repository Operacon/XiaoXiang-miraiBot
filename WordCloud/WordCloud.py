# Copyright(C) 2022 Operacon.
# Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
# https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE

from flask import *
from flask_cors import *
from Service import *

app = Flask(__name__)
CORS(app, supports_credentials=True)


@app.route('/wc', methods=['POST'])
def gene():
    text = request.get_data(as_text=True)
    return send_file(
        io.BytesIO(geneImg(text)),
        mimetype='image/png',
        as_attachment=True,
        download_name='result.jpg'
    )


if __name__ == '__main__':
    app.run(debug=True, host='localhost', port=6785)
