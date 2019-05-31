public class GameData {

    public static Card CARD_SUN_FLOWER = new Card(1, PlantType.Sunflower, "images/cards/card_sunflower.png", 50, 110, 8);
    public static Card CARD_PEAR_SHOOTER = new Card(2, PlantType.Peashooter, "images/cards/card_peashooter.png", 100, 178, 8);
    public static Card CARD_FREEZE_SHOOTER = new Card(3, PlantType.FreezePeashooter, "images/cards/card_freezepeashooter.png", 175, 240, 8);
    public static int INITIAL_SCORE = 150;

    public static class Card {
        private final int pos;
        private final PlantType type;
        private final String image;
        private final int price;
        private final int x;
        private final int y;

        public Card(int pos, PlantType type, String image, int price, int x, int y) {
            this.pos = pos;
            this.type = type;
            this.image = image;
            this.price = price;
            this.x = x;
            this.y = y;
        }

        public int getPos() {
            return pos;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public PlantType getType() {
            return type;
        }

        public String getImage() {
            return image;
        }

        public int getPrice() {
            return price;
        }

    }
}
