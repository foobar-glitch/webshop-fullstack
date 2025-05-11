import mariadb

def init_db(SQL_HOST, SQL_PORT, ROOT_USER_PASSWORD, USER_NAME, USER_PASSWORD, DB_NAME, USERS_TABLE, COOKIE_TABLE, RESET_TABLE, REGISTER_TABLE):
    try:
        root_connection = mariadb.connect(
            host=SQL_HOST,
            port=SQL_PORT,
            user="root",
            passwd="root",
        )
    except mariadb.OperationalError:
        root_connection = mariadb.connect(
            host=SQL_HOST,
            port=SQL_PORT,
            user="root",
            passwd=ROOT_USER_PASSWORD,
        )

    cursor = root_connection.cursor()
    #change password of root to new password
    cursor.execute(f"ALTER USER `root`@`%` IDENTIFIED BY '{ROOT_USER_PASSWORD}'")
    cursor.execute(f"CREATE DATABASE IF NOT EXISTS {DB_NAME}")
    cursor.execute(f"CREATE USER IF NOT EXISTS `{USER_NAME}`@`%` IDENTIFIED BY '{USER_PASSWORD}'")
    cursor.execute(f"GRANT ALL privileges ON `{DB_NAME}`.* TO `{USER_NAME}`@`%`")
    root_connection.close()

    # Connect to database as non root to avoid mitigate damage to db
    user_connection = mariadb.connect(
        host=SQL_HOST,
        user=USER_NAME,
        port=SQL_PORT,
        password=USER_PASSWORD,
        database=DB_NAME
    )

    cursor = user_connection.cursor()
    create_user_table = f"""
    CREATE TABLE IF NOT EXISTS {USERS_TABLE} (
        user_id INT(11) NOT NULL AUTO_INCREMENT,
        username VARCHAR(255) NOT NULL UNIQUE,
        email VARCHAR(255) NOT NULL UNIQUE,
        password VARBINARY(255) NOT NULL,
        salt VARBINARY(255) NOT NULL,
        role VARCHAR(255) NOT NULL,
        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        PRIMARY KEY (user_id)
    );
    """

    create_cookie_table = f"""
    CREATE TABLE IF NOT EXISTS {COOKIE_TABLE} (
        cookie_id INT(11) NOT NULL AUTO_INCREMENT,
        user_id INT(11) NOT NULL,
        cookie_data VARBINARY(255) NOT NULL UNIQUE,
        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        expired_at DATETIME NOT NULL,
        PRIMARY KEY (cookie_id),
        FOREIGN KEY (user_id) REFERENCES {USERS_TABLE}(user_id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
    );
    """

    create_reset_table = f"""
    CREATE TABLE IF NOT EXISTS {RESET_TABLE} (
        reset_id INT(11) NOT NULL AUTO_INCREMENT,
        user_id INT(11) NOT NULL,
        reset_token VARBINARY(255) NOT NULL UNIQUE,
        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        expired_at DATETIME NOT NULL,
        PRIMARY KEY (reset_id),
        FOREIGN KEY (user_id) REFERENCES {USERS_TABLE}(user_id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
    );
    """

    create_temp_user_table = f"""
    CREATE TABLE IF NOT EXISTS temp_users (
        temp_user_id INT(11) NOT NULL AUTO_INCREMENT,
        username VARCHAR(255) NOT NULL UNIQUE,
        email VARCHAR(255) NOT NULL UNIQUE,
        password VARBINARY(255) NOT NULL,
        salt VARBINARY(255) NOT NULL,
        role VARCHAR(255) NOT NULL,
        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        PRIMARY KEY (temp_user_id)
    );
    """

    create_register_table=f"""
    CREATE TABLE IF NOT EXISTS {REGISTER_TABLE} (
        register_id INT(11) NOT NULL AUTO_INCREMENT,
        temp_user_id INT(11) NOT NULL,
        register_token VARBINARY(255) NOT NULL UNIQUE,
        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        expired_at DATETIME NOT NULL,
        PRIMARY KEY (register_id),
        FOREIGN KEY (temp_user_id) REFERENCES temp_users(temp_user_id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
    );
    """

    cursor.execute(create_user_table)
    cursor.execute(create_cookie_table)
    cursor.execute(create_reset_table)
    cursor.execute(create_temp_user_table)
    cursor.execute(create_register_table)


    user_connection.commit()
    user_connection.close()



def create_users(SQL_HOST, SQL_PORT, USER_NAME, USER_PASSWORD, DB_NAME, USERS_TABLE):
    user_connection = mariadb.connect(
        host=SQL_HOST,
        port=SQL_PORT,
        user=USER_NAME,
        password=USER_PASSWORD,
        database=DB_NAME
    )


    cursor = user_connection.cursor()
    create_admin = f"""
        INSERT INTO {USERS_TABLE} (user_id, username, email, password, salt, role, created_at, updated_at)
        VALUES (1, 'admin', 'admin@mail.com', SHA2(CONCAT('admin',UNHEX('47b9bc77d6212e7e')), 256), '47b9bc77d6212e7e', 'admin', NOW(), NOW());
    """

    create_author = f"""
        INSERT INTO {USERS_TABLE} (user_id, username, email, password, salt, role, created_at, updated_at)
        VALUES (2, 'author', 'author@mail.com', SHA2(CONCAT('author',UNHEX('6f7f4b9659cbfbe6')), 256), '6f7f4b9659cbfbe6', 'author', NOW(), NOW());
    """

    create_user = f"""
        INSERT INTO {USERS_TABLE} (user_id, username, email, password, salt, role, created_at, updated_at)
        VALUES  (3, 'user', 'user@mail.com', SHA2(CONCAT('user',UNHEX('391feef6e93e0b29')), 256), '391feef6e93e0b29', 'user', NOW(), NOW());
    """

    cursor.execute(create_admin)
    cursor.execute(create_author)
    cursor.execute(create_user)

    user_connection.commit()
    user_connection.close()


def init_inventory_db(SQL_HOST, SQL_PORT, USER_NAME, USER_PASSWORD, DB_NAME, INVENTORY_TABLE, RATING_TABLE, INVENTORY_IMAGE_TABLE, USERS_TABLE, CART_TABLE):
    user_connection = mariadb.connect(
        host=SQL_HOST,
        user=USER_NAME,
        port=SQL_PORT,
        password=USER_PASSWORD,
        database=DB_NAME
    )

    create_inventory_table=f"""
    CREATE TABLE IF NOT EXISTS {INVENTORY_TABLE} (
        inventory_id INT PRIMARY KEY AUTO_INCREMENT,
        product_name VARCHAR(100) NOT NULL,
        description TEXT NOT NULL,
        price DECIMAL(10, 2) NOT NULL,
        quantity INT NOT NULL,
        category VARCHAR(50) NOT NULL,
        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        );
    """

    create_inventory_image_table=f"""
    CREATE TABLE IF NOT EXISTS {INVENTORY_IMAGE_TABLE} (
        image_id INT PRIMARY KEY AUTO_INCREMENT,
        inventory_id INT NOT NULL,
        image_url VARCHAR(255) NOT NULL,
        alt_text VARCHAR(150) NOT NULL,
        FOREIGN KEY (inventory_id) REFERENCES {INVENTORY_TABLE}(inventory_id) 
            ON DELETE CASCADE
            ON UPDATE CASCADE
        );
    """

    create_rating_table=f"""
    CREATE TABLE IF NOT EXISTS {RATING_TABLE} (
    rating_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    inventory_id INT NOT NULL,
    stars TINYINT NOT NULL,
    comment TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES {USERS_TABLE}(user_id) 
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (inventory_id) REFERENCES {INVENTORY_TABLE}(inventory_id) 
        ON DELETE CASCADE
        ON UPDATE CASCADE
    );
    """

    create_cart_table = f"""
    CREATE TABLE IF NOT EXISTS {CART_TABLE} (
        cart_id INT PRIMARY KEY AUTO_INCREMENT,
        user_id INT NOT NULL,
        inventory_id INT NOT NULL,
        quantity INT NOT NULL DEFAULT 1,
        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (user_id) REFERENCES {USERS_TABLE}(user_id)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
        FOREIGN KEY (inventory_id) REFERENCES {INVENTORY_TABLE}(inventory_id)
            ON DELETE CASCADE
            ON UPDATE CASCADE
    );
"""


    cursor = user_connection.cursor()
    cursor.execute(create_inventory_table)
    cursor.execute(create_inventory_image_table)
    cursor.execute(create_rating_table)
    cursor.execute(create_cart_table)


    user_connection.commit()
    user_connection.close()


def create_inventory(SQL_HOST, SQL_PORT, USER_NAME, USER_PASSWORD, DB_NAME, INVENTORY_TABLE):
    user_connection = mariadb.connect(
        host=SQL_HOST,
        port=SQL_PORT,
        user=USER_NAME,
        password=USER_PASSWORD,
        database=DB_NAME
    )


    cursor = user_connection.cursor()
    airpods_description=f"""Erlebe immersiven Sound mit aktiver Geräusch nterdrückung auf Pro Level Fußnote ³,
    Adaptives Audio für eine ausgewogene Geräuschsteuerung in jeder Umgebung, Transparenzmodus, mit dem du dein 
    Umfeld hören kannst Fußnote ² und die Konversationserkennung, um die Lautstärke deiner Medien nahtlos zu 
    reduzieren, wenn du mit jemanden in der Nähe sprichst. Fußnote ¹⁵ Sowohl die AirPods Pro 2 als auch das kabellose 
    MagSafe Ladecase (USB-C) sind nach IP54 vor Staub, Schweiß und Wasser geschützt Fußnote ¹⁴ und du kannst sie mit 
    der „Wo ist?“ App orten."""

    galaxy_buds_description=""""Lass deine Galaxy Buds3 Pro im Ohr und bleib mit deiner Umgebung verbunden. 
    Dank Galaxy AI erkennen und unterdrücken die Galaxy Buds3 Pro automatisch unnötige Geräusche. Sie passen das 
    Gleichgewicht zwischen ANC und Ambient Mode an deine Umgebung an. So kannst du ungestört deine Lieblingsmusik hören. 
    Die Galaxy Buds3 Pro blockieren störende Außengeräusche, lassen aber wichtige Signale wie Alarme und Sirenen durch, 
    damit du sicher bleibst und nichts verpasst"""

    bosch_WAN28127="""Die Bosch WAN28127 Waschmaschine spart dir Zeit im Alltag. Trägst du gerne Hemden oder Blusen? Dank der Iron Assist Dampffunktion kommen diese weniger zerknittert aus der Trommel. Der Dampf hilft, Falten in deiner Kleidung zu verringern. Dadurch hast du weniger zu bügeln. Wenn es einmal etwas schneller gehen soll, nutzt du die SpeedPerfect Funktion. Damit verkürzt du Waschprogramme um bis zu 65 Prozent. So ist deine Wäsche schneller fertig, doch das Waschergebnis bleibt das gleiche. Mit der Bosch Waschmaschine sparst du nicht nur Zeit beim Waschen und Bügeln. Du sparst auch Energie und Geld. Denn dank der Energieeffizienzklasse A wäschst du besonders effizient. Somit sparst du bis zu 220 € an Energiekosten über die Lebensdauer des Geräts. """

    create_airpods_entry = f"""
        INSERT INTO {INVENTORY_TABLE} (inventory_id, product_name, description, price, quantity, category, created_at, updated_at)
        VALUES (1, 'AirPods Pro 2', '{airpods_description}', 279.00, 195, 'headphones', NOW(), NOW());
    """

    create_airbudds_entry = f"""
        INSERT INTO {INVENTORY_TABLE} (inventory_id, product_name, description, price, quantity, category, created_at, updated_at)
        VALUES (2, 'Galaxy Buds3 Pro', '{galaxy_buds_description}', 179.99, 150, 'headphones', NOW(), NOW());
    """

    create_bosh_entry=f"""INSERT INTO {INVENTORY_TABLE} (inventory_id, product_name, description, price, quantity, category, created_at, updated_at)
        VALUES (3, 'Bosch WAN28127', '{bosch_WAN28127}', 499.99, 100, 'washing machines', NOW(), NOW());"""


    cursor.execute(create_airpods_entry)
    cursor.execute(create_airbudds_entry)
    cursor.execute(create_bosh_entry)

    user_connection.commit()
    user_connection.close()

