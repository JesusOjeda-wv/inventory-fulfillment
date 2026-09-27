import { useState } from 'react'

type OrderFormProps = {
    productId: number
    productName: string
    onOrdered: () => Promise<void>
}

export default function OrderForm({
                                      productId,
                                      productName,
                                      onOrdered,
                                  }: OrderFormProps) {
    const [quantity, setQuantity] = useState('1')
    const [submitting, setSubmitting] = useState(false)
    const [error, setError] = useState('')
    const [message, setMessage] = useState('')

    async function placeOrder() {
        const amount = Number(quantity)

        setError('')
        setMessage('')

        if (
            !Number.isInteger(amount) ||
            amount <= 0 ||
            amount > 2147483647
        ) {
            setError('Enter a positive whole number.')
            return
        }

        setSubmitting(true)

        try {
            const response = await fetch('/api/orders', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    productId,
                    quantity: amount,
                }),
            })

            if (!response.ok) {
                if (response.status === 409) {
                    setError('Not enough stock for this order.')
                } else if (response.status === 400) {
                    setError('Please check your order quantity.')
                } else if (response.status === 404) {
                    setError('This product no longer exists.')
                } else {
                    setError('Could not confirm the order. Check order history before retrying.')
                }

                return
            }

            const order: { orderId: number } = await response.json()

            setMessage(`Order #${order.orderId} placed!`)
            setQuantity('1')

            try {
                await onOrdered()
            } catch {
                setError('Order placed, but stock could not refresh. Reload the page.')
            }
        } catch {
            setError('Could not confirm the order. Check order history before retrying.')
        } finally {
            setSubmitting(false)
        }
    }

    return (
        <form
            onSubmit={(event) => {
                event.preventDefault()
                void placeOrder()
            }}
        >
            <input
                type="number"
                min="1"
                max="2147483647"
                step="1"
                required
                aria-label={`Order quantity for ${productName}`}
                value={quantity}
                onChange={(event) => setQuantity(event.target.value)}
                disabled={submitting}
            />

            <button type="submit" disabled={submitting}>
                {submitting ? 'Placing order...' : 'Place order'}
            </button>

            {message && <p role="status">{message}</p>}
            {error && <p role="alert">{error}</p>}
        </form>
    )
}