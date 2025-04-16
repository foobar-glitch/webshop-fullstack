from dotenv import load_dotenv
import os
from library.mariadb import create_inventory, init_db, create_users, init_inventory_db

load_dotenv("../webapp/backend/.env")
def give_mysql_env():


    SQL_HOST = "127.0.0.1"
    SQL_PORT = 3306#int(os.getenv('MARIADB_PORT'))
    ROOT_USER_PASSWORD = os.getenv('MARIADB_ROOT_PASSWORD')

    USER_NAME = os.getenv('MARIADB_USER')
    USER_PASSWORD = os.getenv('MARIADB_USER_PASSWORD')

    DB_NAME = os.getenv('MARIADB_DATABASE')
    USERS_TABLE = os.getenv('MARIADB_USER_TABLE')
    COOKIE_TABLE = os.getenv('MARIADB_COOKIE_TABLE')
    RESET_TABLE = os.getenv('MARIADB_RESET_TABLE')
    REGISTER_TABLE = os.getenv('MARIADB_REGISTER_TABLE')
    return (SQL_HOST, SQL_PORT, ROOT_USER_PASSWORD, USER_NAME, USER_PASSWORD, DB_NAME, USERS_TABLE, COOKIE_TABLE, RESET_TABLE, REGISTER_TABLE)

def give_inventory_tables():
    INVENTORY_TABLE = os.getenv('MARIADB_INVENTORY_TABLE')
    RATING_TABLE = os.getenv('MARIADB_RATING_TABLE')
    INVENTORY_IMAGE_TABLE = os.getenv('MARIADB_INVENTORY_IMAGE_TABLE')
    CART_TABLE = os.getenv('MARIADB_CART_TABLE')
    return INVENTORY_TABLE, RATING_TABLE, INVENTORY_IMAGE_TABLE, CART_TABLE


if __name__=="__main__":
    (SQL_HOST, SQL_PORT, ROOT_USER_PASSWORD, USER_NAME, USER_PASSWORD, DB_NAME, USERS_TABLE, COOKIE_TABLE, RESET_TABLE, REGISTER_TABLE) = give_mysql_env()
    print("Initializing Databases...")
    init_db(SQL_HOST, SQL_PORT, ROOT_USER_PASSWORD, USER_NAME, USER_PASSWORD, DB_NAME, USERS_TABLE, COOKIE_TABLE, RESET_TABLE, REGISTER_TABLE)

    print("Creating inventory")
    INVENTORY_TABLE, RATING_TABLE, INVENTORY_IMAGE_TABLE, CART_TABLE = give_inventory_tables()
    init_inventory_db(SQL_HOST, SQL_PORT, USER_NAME, USER_PASSWORD, DB_NAME, INVENTORY_TABLE, RATING_TABLE, INVENTORY_IMAGE_TABLE, USERS_TABLE, CART_TABLE)

    print("Creating login data default Entries...")
    create_users(SQL_HOST, SQL_PORT, USER_NAME, USER_PASSWORD, DB_NAME, USERS_TABLE)

    print("Creating Inventory entries...")
    create_inventory(SQL_HOST, SQL_PORT, USER_NAME, USER_PASSWORD, DB_NAME, INVENTORY_TABLE)


    print("MariaDB succesfully created.")

