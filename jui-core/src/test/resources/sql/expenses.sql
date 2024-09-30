-- Creazione della tabella 'category'
CREATE TABLE category (
    id INT AUTO_INCREMENT PRIMARY KEY,  -- Chiave primaria con auto-incremento
    category VARCHAR(255) NOT NULL      -- Campo 'category' di tipo testo
);

-- Creazione della tabella 'expenses'
CREATE TABLE expenses (
    id INT AUTO_INCREMENT PRIMARY KEY,  -- Chiave primaria con auto-incremento
    expense_date DATE NOT NULL,         -- Campo 'data' per la data dell'esborso
    category_id INT,                    -- Campo 'category_id' che fa riferimento alla tabella 'category'
    amount DECIMAL(10, 2) NOT NULL,     -- Campo 'amount' per l'importo
    FOREIGN KEY (category_id) REFERENCES category(id)  -- Chiave esterna verso la tabella 'category'
);

-- Inserimento di 3 categorie nella tabella 'category'
INSERT INTO category (category) VALUES ('Alimentari');
INSERT INTO category (category) VALUES ('Trasporti');
INSERT INTO category (category) VALUES ('Intrattenimento');

-- Inserimento di 10 record nella tabella 'expenses'
INSERT INTO expenses (expense_date, category_id, amount) VALUES ('2024-09-01', 1, 50.00); -- Alimentari
INSERT INTO expenses (expense_date, category_id, amount) VALUES ('2024-09-02', 1, 30.50); -- Alimentari
INSERT INTO expenses (expense_date, category_id, amount) VALUES ('2024-09-03', 2, 15.00); -- Trasporti
INSERT INTO expenses (expense_date, category_id, amount) VALUES ('2024-09-03', 3, 20.00); -- Intrattenimento
INSERT INTO expenses (expense_date, category_id, amount) VALUES ('2024-09-04', 1, 75.00); -- Alimentari
INSERT INTO expenses (expense_date, category_id, amount) VALUES ('2024-09-05', 2, 10.00); -- Trasporti
INSERT INTO expenses (expense_date, category_id, amount) VALUES ('2024-09-06', 2, 12.50); -- Trasporti
INSERT INTO expenses (expense_date, category_id, amount) VALUES ('2024-09-06', 3, 50.00); -- Intrattenimento
INSERT INTO expenses (expense_date, category_id, amount) VALUES ('2024-09-07', 1, 40.00); -- Alimentari
INSERT INTO expenses (expense_date, category_id, amount) VALUES ('2024-09-08', 3, 100.00); -- Intrattenimento
