# Mercury v0.2

Minecraft RPG dungeons server made using [minestom](https://github.com/minestom/Minestom/).

This is fun project started to learn more about Minestom.

## Features
- [x] Item management system
- [x] Item rarity
- [x] Player profiles
- [x] Custom inventory system
- [ ] Entity management system
- [ ] Custom mobs with AI and behaviour
- [ ] Spells
- [ ] Party and multiplayer dungeon exploration
- [ ] Randomly generated dungeon
- [x] Translation system
- [ ] Dungeon mini-games
- [x] MongoDB integration
- [ ] Attributes
- [ ] Discord bot integration
- [ ] Website integration
- [ ] Api

## ToDo
- Improve current item and mob system

## How To Run
1. Clone the repository
```bash
   git clone https://github.com/yourusername/Mercury.git
   cd Mercury
```
2. Build the project
```bash
    ./gradlew build
```
3. Configure environment variable
4. Rename `.env.example` to `.env`
5. Run the server
```bash
    java -jar build/libs/Mercury-0.2.jar
```

## Environment Variables
To run this project, you will need to add the following environment variables to your .env file

`MONGO_USERNAME`

`MONGO_PASSWORD`

`MONGO_LINK`

`DISCORD_BOT_TOKEN` (Optional, if `enabled=false` in `bot.toml`)

## Licence
This project is licensed under the [MIT](https://choosealicense.com/licenses/mit/)

## Credits
- Inventory system is inspired by [inventory-framework](https://github.com/DevNatan/inventory-framework)