import { useEffect, useState } from 'react'
import './App.css'
import OrderHistory from './OrderHistory'
import OrderForm from './OrderForm'
import StockEditor from './StockEditor'

type Product = {
    id: number
    name: string
    price: number
    stock: number
}

export default function App() {
    const [products, setProducts] = useState<Product[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [ordersRefreshKey, setOrdersRefreshKey] = useState(0)

    async function refreshProducts() {
        const response = await fetch('/api/products')

        if (!response.ok) {
            throw new Error('Could not refresh products.')
        }

        const data: Product[] = await response.json()
        setProducts(data)
    }

    useEffect(() => {
        let ignore = false

        async function loadProducts() {
            try {
                const response = await fetch('/api/products')

                if (!response.ok) {
                    throw new Error(`Request failed: ${response.status}`)
                }

                const data: Product[] = await response.json()

                if (!ignore) {
                    setProducts(data)
                }
            } catch {
                if (!ignore) {
                    setError('Could not load products. Check that the backend is running.')
                }
            } finally {
                if (!ignore) {
                    setLoading(false)
                }
            }
        }

        loadProducts()


        return () => {
            ignore = true
        }
    }, [])

    async function handleOrderPlaced() {
        setOrdersRefreshKey((previous) => previous + 1)
        await refreshProducts()
    }


    return (

        <main>
            <h1>Inventory & Fulfillment</h1>
            <p>Product catalog</p>

            {loading && <p role="status">Loading products...</p>}
            {error && <p role="alert">{error}</p>}

            {!loading && !error && (
                products.length === 0 ? (
                    <p>No products available.</p>
                ) : (
                    <table>
                        <thead>
                        <tr>
                            <th scope="col">Product</th>
                            <th scope="col">Price</th>
                            <th scope="col">In stock</th>
                            <th scope="col">Order</th>
                        </tr>
                        </thead>
                        <tbody>
                        {products.map((product) => (
                            <tr key={product.id}>
                                <td>{product.name}</td>
                                <td>${product.price.toFixed(2)}</td>
                                <td>
                                    <StockEditor
                                        productId={product.id}
                                        productName={product.name}
                                        currentStock={product.stock}
                                        onSaved={(newStock) => {
                                            setProducts((previousProducts) =>
                                                previousProducts.map((item) =>
                                                    item.id === product.id
                                                        ? { ...item, stock: newStock }
                                                        : item
                                                )
                                            )
                                        }}
                                        key={`${product.id}-${product.stock}`}
                                    />
                                </td>
                                <td>
                                    <OrderForm
                                        productId={product.id}
                                        productName={product.name}
                                        onOrdered={handleOrderPlaced}
                                    />
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )
            )}
            <OrderHistory refreshKey={ordersRefreshKey} />
        </main>
    )
}