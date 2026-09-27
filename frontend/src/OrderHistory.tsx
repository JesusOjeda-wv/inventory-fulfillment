import { useEffect, useState } from 'react'

type Order = {
    id: number
    productId: number
    productName: string
    quantity: number
    unitPrice: number
    status: string
    createdAt: string
}

type OrderHistoryProps = {
    refreshKey: number
}

export default function OrderHistory({ refreshKey }: OrderHistoryProps) {
    const [orders, setOrders] = useState<Order[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    useEffect(() => {
        let ignore = false

        async function loadOrders() {
            setLoading(true)
            setError('')

            try {
                const response = await fetch('/api/orders')

                if (!response.ok) {
                    throw new Error('Could not load order history.')
                }

                const data: Order[] = await response.json()

                if (!ignore) {
                    setOrders(data)
                }
            } catch {
                if (!ignore) {
                    setError('Could not load order history. Try refreshing the page.')
                }
            } finally {
                if (!ignore) {
                    setLoading(false)
                }
            }
        }

        void loadOrders()

        return () => {
            ignore = true
        }
    }, [refreshKey])

    return (
        <section aria-labelledby="orders-heading">
            <h2 id="orders-heading">Order history</h2>

            {loading && <p role="status">Loading orders...</p>}
            {error && <p role="alert">{error}</p>}

            {!loading && !error && (
                orders.length === 0 ? (
                    <p>No orders yet.</p>
                ) : (
                    <table>
                        <thead>
                        <tr>
                            <th scope="col">Order</th>
                            <th scope="col">Product</th>
                            <th scope="col">Quantity</th>
                            <th scope="col">Total</th>
                            <th scope="col">Status</th>
                            <th scope="col">Placed</th>
                        </tr>
                        </thead>
                        <tbody>
                        {orders.map((order) => (
                            <tr key={order.id}>
                                <td>#{order.id}</td>
                                <td>{order.productName}</td>
                                <td>{order.quantity}</td>
                                <td>
                                    ${(order.unitPrice * order.quantity).toFixed(2)}
                                </td>
                                <td>{order.status}</td>
                                <td>
                                    {new Date(order.createdAt).toLocaleString()}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )
            )}
        </section>
    )
}